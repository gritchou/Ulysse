package org.qualipso.factory.helloworld;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.wsf.spi.annotation.WebContext;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 11 june 2009
 */
@Stateless(name = HelloWorldService.SERVICE_NAME, mappedName = FactoryNamingConvention.SERVICE_PREFIX + HelloWorldService.SERVICE_NAME)
@WebService(endpointInterface = "org.qualipso.factory.helloworld.HelloWorldService", targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + HelloWorldService.SERVICE_NAME, serviceName = HelloWorldService.SERVICE_NAME)
@WebContext(contextRoot = FactoryNamingConvention.WEB_SERVICE_ROOT_MODULE_CONTEXT + "-" + HelloWorldService.SERVICE_NAME, urlPattern = FactoryNamingConvention.WEB_SERVICE_URL_PATTERN_PREFIX + HelloWorldService.SERVICE_NAME)
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class HelloWorldServiceBean implements HelloWorldService {
	
	private static Log logger = LogFactory.getLog(HelloWorldServiceBean.class);
	private MembershipService membership;
	
	public HelloWorldServiceBean() {
	}
	
	@EJB
	public void setMembershipService(MembershipService membership) {
		this.membership = membership;
	}

	public MembershipService getMembershipService() {
		return this.membership;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String sayHelloWorld() throws HelloWorldServiceException {
		logger.info("readName(...) called");
		
		try {
			return membership.getProfilePathForConnectedIdentifier() + " says : Hello World !!";
		} catch (MembershipServiceException e) {
			throw new HelloWorldServiceException(e);
		}
	}

	@Override
	public String[] getResourceTypeList() {
		return new String[0];
	}

	@Override
	public String getServiceName() {
		return HelloWorldService.SERVICE_NAME;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public FactoryResource findResource(String path) throws FactoryException {
		logger.info("findResource(...) called");
		logger.debug("params : path=" + path);
		
		throw new HelloWorldServiceException("No Resource are managed by HelloWorld Service");
	}

}