package org.qualipso.factory.collaboration.ws;

import java.util.HashMap;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

public class OMEUtils {
    private static OMFactory fac = OMAbstractFactory.getOMFactory();
    private static OMNamespace omNs = fac.createOMNamespace(
	    CollaborationWSUtils.NAME_SPACE, "ns");

    public static QName getQName(String localName) {
	return (new QName(CollaborationWSUtils.NAME_SPACE, localName));
    }

    public static OMElement getInfoPayLoad() {
	OMElement method = fac.createOMElement("getInfo", omNs);
	OMElement userElement = fac.createOMElement("username", omNs);
	userElement.addChild(fac.createOMText(userElement,
		CollaborationWSUtils.USER_NAME));
	OMElement pwdElement = fac.createOMElement("password", omNs);
	pwdElement.addChild(fac.createOMText(pwdElement,
		CollaborationWSUtils.USER_PWD));

	method.addChild(userElement);
	method.addChild(pwdElement);

	return method;
    }

    public static String getFirstElement(HashMap valuesMap) throws Exception {
	String id = "";
	Iterator iterator = valuesMap.keySet().iterator();
	while (iterator.hasNext()) {
	    id = (String) valuesMap.get(iterator.next());
	    break;
	}
	return id;
    }
}
