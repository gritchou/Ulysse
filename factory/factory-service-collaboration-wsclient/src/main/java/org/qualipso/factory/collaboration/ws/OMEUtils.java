package org.qualipso.factory.collaboration.ws;

import java.util.HashMap;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

// TODO: Auto-generated Javadoc
/**
 * The Class OMEUtils.
 */
public class OMEUtils {

    /** The fac. */
    private static OMFactory fac = OMAbstractFactory.getOMFactory();

    /** The om ns. */
    private static OMNamespace omNs = fac.createOMNamespace(
	    CollaborationWSUtils.NAME_SPACE, "ns");

    /**
     * Gets the q name.
     * 
     * @param localName the local name
     * 
     * @return the q name
     */
    public static QName getQName(String localName) {
	return (new QName(CollaborationWSUtils.NAME_SPACE, localName));
    }

    /**
     * Gets the info pay load.
     * 
     * @return the info pay load
     */
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

    /**
     * Gets the first element.
     * 
     * @param valuesMap the values map
     * 
     * @return the first element
     * 
     * @throws Exception the exception
     */
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
