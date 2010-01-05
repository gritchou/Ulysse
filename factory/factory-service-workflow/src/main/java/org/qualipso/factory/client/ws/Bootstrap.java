
package org.qualipso.factory.client.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.7-b01-
 * Generated source version: 2.1
 * 
 */
@WebService(name = "bootstrap", targetNamespace = "http://org.qualipso.factory.ws/service/bootstrap")
@SOAPBinding(style = SOAPBinding.Style.RPC)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface Bootstrap {


    /**
     * 
     * @throws BootstrapServiceException_Exception
     */
    @WebMethod
    public void bootstrap()
        throws BootstrapServiceException_Exception
    ;

    /**
     * 
     * @param arg0
     * @return
     *     returns org.qualipso.factory.client.ws.FactoryResource
     * @throws FactoryException_Exception
     * @throws PathNotFoundException_Exception
     * @throws AccessDeniedException_Exception
     * @throws InvalidPathException_Exception
     */
    @WebMethod
    @WebResult(partName = "return")
    public FactoryResource findResource(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0)
        throws AccessDeniedException_Exception, FactoryException_Exception, InvalidPathException_Exception, PathNotFoundException_Exception
    ;

    /**
     * 
     * @return
     *     returns org.qualipso.factory.client.ws.StringArray
     */
    @WebMethod
    @WebResult(partName = "return")
    public StringArray getResourceTypeList();

    /**
     * 
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(partName = "return")
    public String getServiceName();

}
