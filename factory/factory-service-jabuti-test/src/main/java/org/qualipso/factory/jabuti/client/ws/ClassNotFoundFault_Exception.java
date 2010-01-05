
package org.qualipso.factory.jabuti.client.ws;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.7-b01-
 * Generated source version: 2.1
 * 
 */
@WebFault(name = "ClassNotFoundFault", targetNamespace = "http://org.qualipso.factory.ws/service/jabuti")
public class ClassNotFoundFault_Exception
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private ClassNotFoundFault faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public ClassNotFoundFault_Exception(String message, ClassNotFoundFault faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public ClassNotFoundFault_Exception(String message, ClassNotFoundFault faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: org.qualipso.factory.jabuti.client.ws.ClassNotFoundFault
     */
    public ClassNotFoundFault getFaultInfo() {
        return faultInfo;
    }

}
