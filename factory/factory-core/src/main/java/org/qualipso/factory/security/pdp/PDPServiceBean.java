package org.qualipso.factory.security.pdp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.security.pdp.finder.PolicyRepositoryFinderModule;
import org.qualipso.factory.security.repository.PolicyRepositoryService;

import com.sun.xacml.Indenter;
import com.sun.xacml.PDP;
import com.sun.xacml.PDPConfig;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.finder.AttributeFinder;
import com.sun.xacml.finder.AttributeFinderModule;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.PolicyFinderModule;
import com.sun.xacml.finder.ResourceFinder;
import com.sun.xacml.finder.impl.CurrentEnvModule;
import com.sun.xacml.finder.impl.SelectorModule;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 16 june 2009
 */
@Stateless(name = PDPService.SERVICE_NAME, mappedName = FactoryNamingConvention.LOCAL_SERVICE_PREFIX + PDPService.SERVICE_NAME)
@SecurityDomain(value = "JBossWSDigest")
public class PDPServiceBean implements PDPService {

    private static Log logger = LogFactory.getLog(PDPServiceBean.class);
    
    private PolicyRepositoryService repositoryService;
    private PDP pdp;
    
    public PDPServiceBean() {
	}
    
    @PostConstruct
    private void init() throws RuntimeException {
    	try {
            PolicyFinder policyFinder = new PolicyFinder();
            Set<PolicyFinderModule> policyModules = new HashSet<PolicyFinderModule>();
            policyModules.add(new PolicyRepositoryFinderModule(repositoryService.getPolicyRepository()));
            policyFinder.setModules(policyModules);

            CurrentEnvModule envAttributeModule = new CurrentEnvModule();
            SelectorModule selectorAttributeModule = new SelectorModule();

            AttributeFinder attributeFinder = new AttributeFinder();
            List<AttributeFinderModule> attributeModules = new ArrayList<AttributeFinderModule>();
            attributeModules.add(envAttributeModule);
            attributeModules.add(selectorAttributeModule);
            attributeFinder.setModules(attributeModules);

            ResourceFinder resourceFinder = new ResourceFinder();

            pdp = new PDP(new PDPConfig(attributeFinder, policyFinder, resourceFinder));
        } catch (Exception e) {
            throw new RuntimeException("unable to initialize the PDPServiceBean : " + e.getMessage(), e);
        }

        logger.debug("PDPServiceBean initialized");
    }
    
    @EJB
    public void setPolicyRepositoryService(PolicyRepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	public PolicyRepositoryService getPolicyRepositoryService() {
		return repositoryService;
	}  
    
	@Override
	public String query(String request) throws PDPServiceException {
		logger.warn("query(...) called");
		logger.debug("params : request=\r\n" + request);
		RequestCtx requestCtx = null;

        try {
            ByteArrayInputStream is = new ByteArrayInputStream(request.getBytes());
            requestCtx = RequestCtx.getInstance(is);
        } catch (Exception e) {
            throw new PDPServiceException("unable to parse request: ", e);
        }

        ResponseCtx responseCtx = pdp.evaluate(requestCtx);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        responseCtx.encode(os, new Indenter());

        return os.toString();
	}

}
