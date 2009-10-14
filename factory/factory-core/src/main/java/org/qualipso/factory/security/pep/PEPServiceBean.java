package org.qualipso.factory.security.pep;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.qualipso.factory.security.pdp.PDPService;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 16 june 2009
 */
@Stateless(name = "PEP", mappedName = "PEPService")
@SecurityDomain(value = "JBossWSDigest")
public class PEPServiceBean implements PEPService {
	
    private static Log logger = LogFactory.getLog(PEPServiceBean.class);
    
    private PDPService pdp;
    
    public PEPServiceBean() {
	}
    
    @EJB
	public void setPDPService(PDPService pdp) {
		this.pdp = pdp;
	}

	public PDPService getPDPService() {
		return this.pdp;
	}
    
	@Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void checkSecurity(String subject, String object, String action) throws PEPServiceException {
		logger.warn("checkSecurity(...) called");
		logger.debug("params : subject=" + subject + ", object=" + object + ", action=" + action);
		
		String request = PEPServiceHelper.buildRequest(subject, object, action);
		logger.debug("request built : ");
		logger.debug(request);
		String response;
		try {
			response = pdp.query(request);
		} catch ( Exception e ) {
			throw new PEPServiceException(e);
		}
		logger.debug("response received : ");
		logger.debug(response);
		
		XACMLResponseStatus status = PEPServiceHelper.getResponseStatus(response);
		if ( !status.equals(XACMLResponseStatus.PERMIT) ) {
			throw new PEPServiceException("check security failed, access control status is " + status);
		}
	}

}
