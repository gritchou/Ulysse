
package org.qualipso.factory.svn.client.ws;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.7-b01-
 * Generated source version: 2.1
 * 
 */
@WebFault(name = "FactoryException", targetNamespace = "http://org.qualipso.factory.ws/service/membership")
public class FactoryException_Exception
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private FactoryException faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public FactoryException_Exception(String message, FactoryException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public FactoryException_Exception(String message, FactoryException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: org.qualipso.factory.svn.client.ws.FactoryException
     */
    public FactoryException getFaultInfo() {
        return faultInfo;
    }

}
