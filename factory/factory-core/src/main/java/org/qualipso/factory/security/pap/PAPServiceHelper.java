/*
 *
 * Qualipso Factory
 * Copyright (C) 2006-2010 INRIA
 * http://www.inria.fr - molli@loria.fr
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of LGPL. See licenses details in LGPL.txt
 *
 * Initial authors :
 *
 * Jérôme Blanchard / INRIA
 * Pascal Molli / Nancy Université
 * Gérald Oster / Nancy Université
 * Christophe Bouthier / INRIA
 * 
 */
package org.qualipso.factory.security.pap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


/**
 * Helper class to generate and modify XACML policies.
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 29 May 2009
 */
public class PAPServiceHelper {
    private static final String XACML20_CONTEXT_NS = "urn:oasis:names:tc:xacml:2.0:context:schema:os";
    private static final String XACML20_POLICY_NS = "urn:oasis:names:tc:xacml:2.0:policy:schema:os";
    private static final String XACML20_CONTEXT_LOC = "http://docs.oasis-open.org/xacml/2.0/access_control-xacml-2.0-context-schema-os.xsd";
    private static final String XACML20_POLICY_LOC = "http://docs.oasis-open.org/xacml/2.0/access_control-xacml-2.0-policy-schema-os.xsd";
    private static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String RULE_COMB_ALG_ID = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:permit-overrides";

    // private static final String POLICY_ID_PREFIX = "org:qualipso:pdp:xacml:2.0:policy:";

    /**
     * Build an XACML policy depending on informations provided.
     *
     * @param policyId the policy ID
     * @param subject the subject
     * @param object the oject (resource path)
     * @param actions a String array for actions to permit.
     * @return
     */
    public static String buildPolicy(String policyId, String subject, String object, String[] actions) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element root = doc.createElement("Policy");
            doc.appendChild(root);
            // root.setAttribute("xmlns", XACML20_POLICY_NS);
            root.setAttribute("PolicyId", policyId);
            root.setAttribute("RuleCombiningAlgId", RULE_COMB_ALG_ID);

            // root.setAttribute("xmlns:xacml-context", XACML20_CONTEXT_NS);
            // root.setAttribute("xmlns:xsi", XSI_NS);
            // root.setAttributeNS(XSI_NS, "xsi:schemaLocation",
            // XACML20_POLICY_NS + " " + XACML20_POLICY_LOC + " " + XACML20_CONTEXT_NS + " " + XACML20_CONTEXT_LOC);
            Element target = doc.createElement("Target");
            root.appendChild(target);

            // Resources
            Element resources = doc.createElement("Resources");
            resources.appendChild(buildResourceElement(doc, object));
            target.appendChild(resources);
            root.appendChild(target);

            // Rule
            Element rule = doc.createElement("Rule");
            rule.setAttribute("RuleId", subject);
            rule.setAttribute("Effect", "Permit");

            Element ruleTarget = doc.createElement("Target");

            // Subjects :
            Element subjects = doc.createElement("Subjects");
            subjects.appendChild(buildSubjectElement(doc, subject));
            ruleTarget.appendChild(subjects);

            // Actions
            if (actions != null) {
                Element actionsElement = doc.createElement("Actions");

                for (String a : actions) {
                    actionsElement.appendChild(buildActionElement(doc, a));
                }

                ruleTarget.appendChild(actionsElement);
            }

            rule.appendChild(ruleTarget);
            root.appendChild(rule);

            // Deny for others rule :
            Element rule2 = doc.createElement("Rule");
            rule2.setAttribute("RuleId", "DenyForOthers");
            rule2.setAttribute("Effect", "Deny");

            root.appendChild(rule2);

            TransformerFactory xFactory = TransformerFactory.newInstance();
            Transformer aTransformer = xFactory.newTransformer();
            aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
            aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            Source src = new DOMSource(doc);

            ByteArrayOutputStream bDoc = new ByteArrayOutputStream();

            Result dest = new StreamResult(new OutputStreamWriter(bDoc));
            aTransformer.transform(src, dest);

            return bDoc.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Build the owner XACML policy.
     *
     * @param policyId the policy ID
     * @param subject the subject
     * @param object the object (resource path)
     * @return
     */
    public static String buildOwnerPolicy(String policyId, String subject, String object) {
        try {
            return buildPolicy(policyId, subject, object, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Add a rule to an existing XACML policy.
     *
     * @param policy the String representation of the policy.
     * @param subject the subject for the new rule
     * @param actions the String array of actions to allow for this subject
     * @return a String representation of the new XACML policy
     * @throws PAPServiceException
     */
    public static String addRuleToPolicy(String policy, String subject, String[] actions)
        throws PAPServiceException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            Document doc = docBuilder.parse(new ByteArrayInputStream(policy.getBytes()));

            // Check if a rule already exists for this subject
            NodeList rules = doc.getElementsByTagName("Rule");

            for (int i = 0; i < rules.getLength(); i++) {
                Node rule = rules.item(i);
                NamedNodeMap attr = rule.getAttributes();

                if (subject.equals(attr.getNamedItem("RuleId").getNodeValue())) {
                    throw new PAPServiceException("unable to add a rule to policy : a rule already exists for this subject");
                }
            }

            // Rule
            Element rule = doc.createElement("Rule");
            rule.setAttribute("RuleId", subject);
            rule.setAttribute("Effect", "Permit");

            Element ruleTarget = doc.createElement("Target");

            if ((subject != null) && !subject.equals("")) {
                // Subjects :
                Element subjects = doc.createElement("Subjects");
                subjects.appendChild(buildSubjectElement(doc, subject));
                ruleTarget.appendChild(subjects);
            }

            if (actions.length != 0) {
                // Actions
                Element actionsElement = doc.createElement("Actions");

                for (String a : actions) {
                    actionsElement.appendChild(buildActionElement(doc, a));
                }

                ruleTarget.appendChild(actionsElement);
            }

            rule.appendChild(ruleTarget);

            doc.getFirstChild().insertBefore(rule, rules.item(rules.getLength() - 1));

            TransformerFactory xFactory = TransformerFactory.newInstance();
            Transformer aTransformer = xFactory.newTransformer();
            aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
            aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            Source src = new DOMSource(doc);

            ByteArrayOutputStream bDoc = new ByteArrayOutputStream();

            Result dest = new StreamResult(new OutputStreamWriter(bDoc));
            aTransformer.transform(src, dest);

            return bDoc.toString();
        } catch (Exception e) {
            throw new PAPServiceException(e);
        }
    }

    /**
     * Remove an existing rule from an XACML policy
     *
     * @param policy a String representation of the policy
     * @param subject the subject of the rule to remove
     * @return the String representation of the new policy.
     * @throws PAPServiceException
     */
    public static String removeRuleFromPolicy(String policy, String subject)
        throws PAPServiceException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            Document doc = docBuilder.parse(new ByteArrayInputStream(policy.getBytes()));

            // Check if a rule already exists for this subject
            NodeList rules = doc.getElementsByTagName("Rule");
            boolean ruleFound = false;

            for (int i = 0; i < rules.getLength(); i++) {
                Node rule = rules.item(i);
                NamedNodeMap attr = rule.getAttributes();

                if (subject.equals(attr.getNamedItem("RuleId").getNodeValue())) {
                    ruleFound = true;
                    doc.getFirstChild().removeChild(rule);
                }
            }

            if (!ruleFound) {
                throw new PAPServiceException("unable to remove a rule from policy : no rule found for this subject");
            }

            TransformerFactory xFactory = TransformerFactory.newInstance();
            Transformer aTransformer = xFactory.newTransformer();
            aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
            aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            Source src = new DOMSource(doc);

            ByteArrayOutputStream bDoc = new ByteArrayOutputStream();

            Result dest = new StreamResult(new OutputStreamWriter(bDoc));
            aTransformer.transform(src, dest);

            return bDoc.toString();
        } catch (Exception e) {
            throw new PAPServiceException(e);
        }
    }

    private static Element buildResourceElement(Document doc, String object) {
        Element resource = doc.createElement("Resource");

        Element resourceMatch = doc.createElement("ResourceMatch");
        resourceMatch.setAttribute("MatchId", "urn:oasis:names:tc:xacml:1.0:function:string-equal");
        resource.appendChild(resourceMatch);

        Element attributeValue2 = doc.createElement("AttributeValue");
        attributeValue2.setAttribute("DataType", "http://www.w3.org/2001/XMLSchema#string");
        attributeValue2.appendChild(doc.createTextNode(object));
        resourceMatch.appendChild(attributeValue2);

        Element resourceAttributeDesignator = doc.createElement("ResourceAttributeDesignator");
        resourceAttributeDesignator.setAttribute("AttributeId", "urn:oasis:names:tc:xacml:1.0:resource:resource-id");
        resourceAttributeDesignator.setAttribute("DataType", "http://www.w3.org/2001/XMLSchema#string");
        resourceMatch.appendChild(resourceAttributeDesignator);

        return resource;
    }

    private static Element buildSubjectElement(Document doc, String subject) {
        Element subj = doc.createElement("Subject");

        Element subjectMatch = doc.createElement("SubjectMatch");
        subjectMatch.setAttribute("MatchId", "urn:oasis:names:tc:xacml:1.0:function:string-equal");
        subj.appendChild(subjectMatch);

        Element attributeValue = doc.createElement("AttributeValue");
        attributeValue.setAttribute("DataType", "http://www.w3.org/2001/XMLSchema#string");
        attributeValue.appendChild(doc.createTextNode(subject));
        subjectMatch.appendChild(attributeValue);

        Element subjectAttributeDesignator = doc.createElement("SubjectAttributeDesignator");
        subjectAttributeDesignator.setAttribute("AttributeId", "urn:oasis:names:tc:xacml:1.0:subject:subject-id");
        subjectAttributeDesignator.setAttribute("DataType", "http://www.w3.org/2001/XMLSchema#string");
        subjectMatch.appendChild(subjectAttributeDesignator);

        return subj;
    }

    private static Element buildActionElement(Document doc, String action) {
        Element act = doc.createElement("Action");

        Element actionMatch = doc.createElement("ActionMatch");
        actionMatch.setAttribute("MatchId", "urn:oasis:names:tc:xacml:1.0:function:string-equal");
        act.appendChild(actionMatch);

        Element attributeValue3 = doc.createElement("AttributeValue");
        attributeValue3.setAttribute("DataType", "http://www.w3.org/2001/XMLSchema#string");
        attributeValue3.appendChild(doc.createTextNode(action));
        actionMatch.appendChild(attributeValue3);

        Element actionAttributeDesignator = doc.createElement("ActionAttributeDesignator");
        actionAttributeDesignator.setAttribute("AttributeId", "urn:oasis:names:tc:xacml:1.0:action:action-id");
        actionAttributeDesignator.setAttribute("DataType", "http://www.w3.org/2001/XMLSchema#string");
        actionMatch.appendChild(actionAttributeDesignator);

        return act;
    }
}
