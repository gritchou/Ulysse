package org.qualipso.factory.security.pdp.finder;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.qualipso.factory.security.repository.PolicyRepository;
import org.qualipso.factory.security.repository.PolicyRepositoryException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.xacml.AbstractPolicy;
import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.MatchResult;
import com.sun.xacml.Policy;
import com.sun.xacml.PolicyMetaData;
import com.sun.xacml.PolicySet;
import com.sun.xacml.Target;
import com.sun.xacml.TargetMatch;
import com.sun.xacml.TargetSection;
import com.sun.xacml.combine.PermitOverridesPolicyAlg;
import com.sun.xacml.combine.PolicyCombiningAlgorithm;
import com.sun.xacml.ctx.Status;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.PolicyFinderModule;
import com.sun.xacml.finder.PolicyFinderResult;


public class PolicyRepositoryFinderModule extends PolicyFinderModule {
    private static URI parentPolicyId = null;
    private Log logger = LogFactory.getLog(this.getClass());
    private PolicyFinder finder;
    private PolicyRepository repository;
    private Target target = null;
    private PolicyCombiningAlgorithm combiningAlg = null;

    public PolicyRepositoryFinderModule(PolicyRepository repository) {
        logger.debug("PolicyRepositoryFinderModule created");
        this.repository = repository;
        target = new Target(new TargetSection(null, TargetMatch.SUBJECT, PolicyMetaData.XACML_VERSION_2_0),
                new TargetSection(null, TargetMatch.RESOURCE, PolicyMetaData.XACML_VERSION_2_0),
                new TargetSection(null, TargetMatch.ACTION, PolicyMetaData.XACML_VERSION_2_0),
                new TargetSection(null, TargetMatch.ENVIRONMENT, PolicyMetaData.XACML_VERSION_2_0));
        combiningAlg = new PermitOverridesPolicyAlg();

        try {
            parentPolicyId = new URI("urn:com:sun:xacml:support:finder:dynamic-policy-set");
        } catch (URISyntaxException e) {
            //
        }
    }

    @Override
    public void init(PolicyFinder finder) {
        this.finder = finder;
        logger.debug("PolicyRepositoryFinderModule initialized");
    }

    public boolean isRequestSupported() {
        return true;
    }

    @Override
    public PolicyFinderResult findPolicy(EvaluationCtx context) {
        logger.debug("findPolicy(EvaluationCtx) called");

        List<AbstractPolicy> matchingPolicies = new Vector<AbstractPolicy>();

        try {
            Map<String, byte[]> potentialPolicies = repository.getPolicies(context);
            logger.debug("potential policies : " + potentialPolicies.size());

            for (String id : potentialPolicies.keySet()) {
                AbstractPolicy policy = readPolicy(potentialPolicies.get(id));

                MatchResult match = policy.match(context);
                int result = match.getResult();

                if (result == MatchResult.INDETERMINATE) {
                    return new PolicyFinderResult(match.getStatus());
                }

                if (result == MatchResult.MATCH) {
                    matchingPolicies.add(policy);
                }
            }
        } catch (PolicyRepositoryException pse) {
            logger.error("unable to get policies from repository", pse);

            List<String> codes = new ArrayList<String>();
            codes.add(Status.STATUS_PROCESSING_ERROR);

            return new PolicyFinderResult(new Status(codes, pse.getMessage()));
        }

        logger.debug("matching policies : " + matchingPolicies.size());

        switch (matchingPolicies.size()) {
        case 0:
            return new PolicyFinderResult();

        case 1:
            return new PolicyFinderResult(matchingPolicies.get(0));

        default:
            return new PolicyFinderResult(new PolicySet(parentPolicyId, combiningAlg, target, matchingPolicies));
        }
    }

    private AbstractPolicy readPolicy(byte[] data) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);

            DocumentBuilder db = null;

            factory.setNamespaceAware(false);
            factory.setValidating(false);

            db = factory.newDocumentBuilder();

            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            Document doc = db.parse(bais);

            Element root = doc.getDocumentElement();
            String name = root.getTagName();

            if (name.equals("Policy")) {
                return Policy.getInstance(root);
            } else if (name.equals("PolicySet")) {
                return PolicySet.getInstance(root, finder);
            } else {
                logger.error("error reading policy : unknown root document type : " + name);
            }
        } catch (Exception e) {
            logger.error("error reading policy : " + e.getMessage());
        }

        return null;
    }
}