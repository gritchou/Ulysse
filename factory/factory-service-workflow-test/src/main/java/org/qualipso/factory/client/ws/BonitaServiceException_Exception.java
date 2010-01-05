
package org.qualipso.factory.client.ws;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.7-b01-
 * Generated source version: 2.1
 * 
 */
@WebFault(name = "BonitaServiceException", targetNamespace = "http://org.qualipso.factory.ws/service/workflow")
public class BonitaServiceException_Exception
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private BonitaServiceException faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public BonitaServiceException_Exception(String message, BonitaServiceException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public BonitaServiceException_Exception(String message, BonitaServiceException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: org.qualipso.factory.client.ws.BonitaServiceException
     */
    public BonitaServiceException getFaultInfo() {
        return faultInfo;
    }

}
