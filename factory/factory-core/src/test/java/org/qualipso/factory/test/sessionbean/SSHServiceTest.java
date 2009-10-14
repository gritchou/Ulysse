package org.qualipso.factory.test.sessionbean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.qualipso.factory.ssh.SSHServiceBean;

import com.bm.testsuite.BaseSessionBeanFixture;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 23 September 2009
 */
public class SSHServiceTest extends BaseSessionBeanFixture<SSHServiceBean> {

	private static Log logger = LogFactory.getLog(SSHServiceTest.class);

	@SuppressWarnings("unchecked")
	private static final Class[] usedBeans = { };

	public SSHServiceTest() {
		super(SSHServiceBean.class, usedBeans);
	}

	public void testOne() {
		
	}
}
