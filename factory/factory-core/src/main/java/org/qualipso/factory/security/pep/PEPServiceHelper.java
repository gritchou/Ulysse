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
 *
 */
package org.qualipso.factory.security.pep;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
public class PEPServiceHelper {
	
	public static String buildRequest(String subject, String object, String action) throws PEPServiceException {
    	try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element root = doc.createElement("Request");
            doc.appendChild(root);

            // Subject :
            Element subj = doc.createElement("Subject");
            subj.setAttribute("SubjectCategory", "urn:oasis:names:tc:xacml:1.0:subject-category:access-subject");

            Element sAttribute = doc.createElement("Attribute");
            sAttribute.setAttribute("AttributeId", "urn:oasis:names:tc:xacml:1.0:subject:subject-id");
            sAttribute.setAttribute("DataType", "http://www.w3.org/2001/XMLSchema#string");

            Element sAttributeValue = doc.createElement("AttributeValue");
            sAttributeValue.appendChild(doc.createTextNode(subject));
            sAttribute.appendChild(sAttributeValue);

            subj.appendChild(sAttribute);
            root.appendChild(subj);

            // Resource :
            Element resource = doc.createElement("Resource");

            Element rAttribute = doc.createElement("Attribute");
            rAttribute.setAttribute("AttributeId", "urn:oasis:names:tc:xacml:1.0:resource:resource-id");
            rAttribute.setAttribute("DataType", "http://www.w3.org/2001/XMLSchema#string");

            Element rAttributeValue = doc.createElement("AttributeValue");
            rAttributeValue.appendChild(doc.createTextNode(object));
            rAttribute.appendChild(rAttributeValue);

            resource.appendChild(rAttribute);
            root.appendChild(resource);

            // Resource :
            Element act = doc.createElement("Action");

            Element rAction = doc.createElement("Attribute");
            rAction.setAttribute("AttributeId", "urn:oasis:names:tc:xacml:1.0:action:action-id");
            rAction.setAttribute("DataType", "http://www.w3.org/2001/XMLSchema#string");

            Element rActionValue = doc.createElement("AttributeValue");
            rActionValue.appendChild(doc.createTextNode(action));
            rAction.appendChild(rActionValue);

            act.appendChild(rAction);
            root.appendChild(act);

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
            throw new PEPServiceException(e);
        }
    }

    public static XACMLResponseStatus getResponseStatus(String response) throws PEPServiceException {
    	try {
            if ( response.indexOf("<Decision>Permit</Decision>") > -1 ) {
            	return XACMLResponseStatus.PERMIT;
            }
            if ( response.indexOf("<Decision>Deny</Decision>") > -1 ) {
            	return XACMLResponseStatus.DENY;
            }
            if ( response.indexOf("<Decision>Indeterminate</Decision>") > -1 ) {
            	return XACMLResponseStatus.INDETERMINATE;
            }
            if ( response.indexOf("<Decision>NotApplicable</Decision>") > -1 ) {
            	return XACMLResponseStatus.NOTAPPLICABLE;
            }
            throw new PEPServiceException("unable to parse response status decision : " + response);
    	} catch (Exception e) {
            throw new PEPServiceException("error in parsing response", e);
        }
    }
}
