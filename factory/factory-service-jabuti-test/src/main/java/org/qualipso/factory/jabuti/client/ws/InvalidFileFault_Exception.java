
package org.qualipso.factory.jabuti.client.ws;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.7-b01-
 * Generated source version: 2.1
 * 
 */
@WebFault(name = "InvalidFileFault", targetNamespace = "http://org.qualipso.factory.ws/service/jabuti")
public class InvalidFileFault_Exception
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private InvalidFileFault faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public InvalidFileFault_Exception(String message, InvalidFileFault faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public InvalidFileFault_Exception(String message, InvalidFileFault faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: org.qualipso.factory.jabuti.client.ws.InvalidFileFault
     */
    public InvalidFileFault getFaultInfo() {
        return faultInfo;
    }

}
