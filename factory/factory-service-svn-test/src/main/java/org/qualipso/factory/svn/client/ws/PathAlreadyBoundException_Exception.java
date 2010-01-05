
package org.qualipso.factory.svn.client.ws;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.7-b01-
 * Generated source version: 2.1
 * 
 */
@WebFault(name = "PathAlreadyBoundException", targetNamespace = "http://org.qualipso.factory.ws/service/membership")
public class PathAlreadyBoundException_Exception
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private PathAlreadyBoundException faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public PathAlreadyBoundException_Exception(String message, PathAlreadyBoundException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public PathAlreadyBoundException_Exception(String message, PathAlreadyBoundException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: org.qualipso.factory.svn.client.ws.PathAlreadyBoundException
     */
    public PathAlreadyBoundException getFaultInfo() {
        return faultInfo;
    }

}
