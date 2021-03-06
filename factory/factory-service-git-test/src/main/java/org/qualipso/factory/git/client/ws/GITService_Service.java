
package org.qualipso.factory.git.client.ws;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.7-b01-
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "GITService", targetNamespace = "http://org.qualipso.factory.ws/service/git", wsdlLocation = "http://localhost:8080/factory-service-git/git?wsdl")
public class GITService_Service
    extends Service
{

    private final static URL GITSERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(org.qualipso.factory.git.client.ws.GITService_Service.class.getName());

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = org.qualipso.factory.git.client.ws.GITService_Service.class.getResource(".");
            url = new URL(baseUrl, "http://localhost:8080/factory-service-git/git?wsdl");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'http://localhost:8080/factory-service-git/git?wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        GITSERVICE_WSDL_LOCATION = url;
    }

    public GITService_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public GITService_Service() {
        super(GITSERVICE_WSDL_LOCATION, new QName("http://org.qualipso.factory.ws/service/git", "GITService"));
    }

    /**
     * 
     * @return
     *     returns GITService
     */
    @WebEndpoint(name = "GITServicePort")
    public GITService getGITServicePort() {
        return super.getPort(new QName("http://org.qualipso.factory.ws/service/git", "GITServicePort"), GITService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns GITService
     */
    @WebEndpoint(name = "GITServicePort")
    public GITService getGITServicePort(WebServiceFeature... features) {
        return super.getPort(new QName("http://org.qualipso.factory.ws/service/git", "GITServicePort"), GITService.class, features);
    }

}
