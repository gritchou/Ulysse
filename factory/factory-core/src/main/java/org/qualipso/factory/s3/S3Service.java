package org.qualipso.factory.s3;

import javax.ejb.Remote;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryService;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 23 june 2009
 */
@Remote
@WebService(name = "S3Service", targetNamespace = "http://org.qualipso.factory.ws/service/s3")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface S3Service extends FactoryService {
	
	

}
