package org.qualipso.factory.s3;

import javax.ejb.Remote;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryService;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 23 june 2009
 */
@Remote
@WebService(name = S3Service.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + S3Service.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface S3Service extends FactoryService {
	
	public static final String SERVICE_NAME = "s3";
	public static final String[] RESOURCE_TYPE_LIST = new String[] {};

}
