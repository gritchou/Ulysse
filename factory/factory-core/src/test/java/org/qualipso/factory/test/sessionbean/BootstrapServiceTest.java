package org.qualipso.factory.test.sessionbean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.qualipso.factory.binding.entity.Node;
import org.qualipso.factory.bootstrap.BootstrapServiceBean;
import org.qualipso.factory.core.entity.File;
import org.qualipso.factory.core.entity.Folder;
import org.qualipso.factory.membership.entity.Profile;

import com.bm.testsuite.BaseSessionBeanFixture;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 4 September 2009
 */
public class BootstrapServiceTest  extends BaseSessionBeanFixture<BootstrapServiceBean> {

	private static Log logger = LogFactory.getLog(BootstrapServiceTest.class);

	@SuppressWarnings("unchecked")
	private static final Class[] usedBeans = { Folder.class, File.class, Profile.class, Node.class };

	public BootstrapServiceTest() {
		super(BootstrapServiceBean.class, usedBeans);
	}

	@Test
	public void testBootstrap() {
		logger.debug("Bootstrap Service Version : " + BootstrapServiceBean.VERSION);
		
	}

}
