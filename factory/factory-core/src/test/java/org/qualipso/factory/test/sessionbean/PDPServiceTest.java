package org.qualipso.factory.test.sessionbean;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.qualipso.factory.security.pdp.PDPServiceBean;
import org.qualipso.factory.security.pep.PEPServiceHelper;
import org.qualipso.factory.security.repository.FilePolicyRepository;

import com.bm.testsuite.BaseSessionBeanFixture;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 2 September 2009
 */
public class PDPServiceTest extends BaseSessionBeanFixture<PDPServiceBean> {

	private static Log logger = LogFactory.getLog(PDPServiceTest.class);

	@SuppressWarnings("unchecked")
	private static final Class[] usedBeans = {};

	public PDPServiceTest() {
		super(PDPServiceBean.class, usedBeans);
	}

	public void setUp() throws Exception {
		super.setUp();
		logger.debug("setting up pdp service");
	}
	
	public void tearDown() throws Exception {
		File file = ((FilePolicyRepository) getBeanToTest().getPolicyRepositoryService().getPolicyRepository()).getRepositoryFolder();

        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();

            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        }

        file.delete();
	}

	public void testRequest1() {
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream("requests/request-01.xml");
            StringBuffer request = new StringBuffer();
            byte[] buffer = new byte[1024];
            int nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                request.append(new String(buffer, 0, nbRead));
            }

            String response = getBeanToTest().query(request.toString());

            assertTrue(response.indexOf("NotApplicable") != -1);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

	public void testRequest2() {
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream("policies/policy-01.xml");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            getBeanToTest().getPolicyRepositoryService().getPolicyRepository().addPolicy("policy01",baos.toByteArray());

            is = ClassLoader.getSystemResourceAsStream("requests/request-01.xml");

            StringBuffer request = new StringBuffer();
            buffer = new byte[1024];
            nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                request.append(new String(buffer, 0, nbRead));
            }

            String response = getBeanToTest().query(request.toString());

            assertTrue(response.indexOf("Permit") != -1);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testRequest3() {
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream("policies/policy-01.xml");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            getBeanToTest().getPolicyRepositoryService().getPolicyRepository().addPolicy("policy01",baos.toByteArray());

            is = ClassLoader.getSystemResourceAsStream("policies/policy-02.xml");
            baos = new ByteArrayOutputStream();
            buffer = new byte[1024];
            nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            getBeanToTest().getPolicyRepositoryService().getPolicyRepository().addPolicy("policy02",baos.toByteArray());

            is = ClassLoader.getSystemResourceAsStream("requests/request-02.xml");

            StringBuffer request1 = new StringBuffer();
            buffer = new byte[1024];
            nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                request1.append(new String(buffer, 0, nbRead));
            }

            String response1 = getBeanToTest().query(request1.toString());

            is = ClassLoader.getSystemResourceAsStream("requests/request-03.xml");

            StringBuffer request2 = new StringBuffer();
            buffer = new byte[1024];
            nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                request2.append(new String(buffer, 0, nbRead));
            }

            String response2 = getBeanToTest().query(request2.toString());

            assertTrue(response1.indexOf("NotApplicable") != -1);
            assertTrue(response2.indexOf("Permit") != -1);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testRequest5() {
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream("policies/policy-04.xml");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            getBeanToTest().getPolicyRepositoryService().getPolicyRepository().addPolicy("policy04",baos.toByteArray());

            String request1 = PEPServiceHelper.buildRequest("root", "/test/testuri", "read");
            String request2 = PEPServiceHelper.buildRequest("root", "/test/testuri", "create");
            String request3 = PEPServiceHelper.buildRequest("root", "/test/testuri", "update");
            String request4 = PEPServiceHelper.buildRequest("root", "/test/testuri", "delete");
            String request5 = PEPServiceHelper.buildRequest("root", "/test/testuri", "admin");
            String request6 = PEPServiceHelper.buildRequest("root", "/test/testuri", "nothing");
            String request7 = PEPServiceHelper.buildRequest("anotheruser", "/test/testuri", "read");

            String response1 = getBeanToTest().query(request1);
            String response2 = getBeanToTest().query(request2);
            String response3 = getBeanToTest().query(request3);
            String response4 = getBeanToTest().query(request4);
            String response5 = getBeanToTest().query(request5);
            String response6 = getBeanToTest().query(request6);
            String response7 = getBeanToTest().query(request7);

            assertTrue(response1.indexOf("Permit") != -1);
            assertTrue(response2.indexOf("Permit") != -1);
            assertTrue(response3.indexOf("Permit") != -1);
            assertTrue(response4.indexOf("Permit") != -1);
            assertTrue(response5.indexOf("Permit") != -1);
            assertTrue(response6.indexOf("Permit") != -1);
            assertTrue(response7.indexOf("Permit") == -1);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testRequest6() {
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream("policies/policy-05.xml");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            getBeanToTest().getPolicyRepositoryService().getPolicyRepository().addPolicy("policy05",baos.toByteArray());

            String request1 = PEPServiceHelper.buildRequest("guest", "/test/testuri", "read");
            String request2 = PEPServiceHelper.buildRequest("guest", "/test/anotheruri", "read");
            String request3 = PEPServiceHelper.buildRequest("create", "/test/testuri", "delete");

            String response1 = getBeanToTest().query(request1);
            String response2 = getBeanToTest().query(request2);
            String response3 = getBeanToTest().query(request3);

            assertTrue(response1.indexOf("Permit") != -1);
            assertTrue(response2.indexOf("Permit") != -1);
            assertTrue(response3.indexOf("Permit") == -1);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testRequest7() {
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream("policies/policy-06.xml");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            getBeanToTest().getPolicyRepositoryService().getPolicyRepository().addPolicy("policy06",baos.toByteArray());

            is = ClassLoader.getSystemResourceAsStream("policies/policy-07.xml");
            baos = new ByteArrayOutputStream();
            buffer = new byte[1024];
            nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            getBeanToTest().getPolicyRepositoryService().getPolicyRepository().addPolicy("policy07",baos.toByteArray());

            String request11 = PEPServiceHelper.buildRequest("user1", "/tests/node1", "read");
            String request12 = PEPServiceHelper.buildRequest("user1", "/tests/node1", "create");
            String request13 = PEPServiceHelper.buildRequest("user1", "/tests/node1", "update");
            String request14 = PEPServiceHelper.buildRequest("user1", "/tests/node1", "delete");
            String request15 = PEPServiceHelper.buildRequest("user1", "/tests/node1", "owner");

            String request21 = PEPServiceHelper.buildRequest("user2", "/tests/node1", "read");
            String request22 = PEPServiceHelper.buildRequest("user2", "/tests/node1", "create");
            String request23 = PEPServiceHelper.buildRequest("user2", "/tests/node1", "update");
            String request24 = PEPServiceHelper.buildRequest("user2", "/tests/node1", "delete");
            String request25 = PEPServiceHelper.buildRequest("user2", "/tests/node1", "owner");

            String request31 = PEPServiceHelper.buildRequest("user1", "/tests/node2", "read");
            String request32 = PEPServiceHelper.buildRequest("user1", "/tests/node2", "create");
            String request33 = PEPServiceHelper.buildRequest("user1", "/tests/node2", "update");
            String request34 = PEPServiceHelper.buildRequest("user1", "/tests/node2", "delete");
            String request35 = PEPServiceHelper.buildRequest("user1", "/tests/node2", "owner");

            String request41 = PEPServiceHelper.buildRequest("user2", "/tests/node2", "read");
            String request42 = PEPServiceHelper.buildRequest("user2", "/tests/node2", "create");
            String request43 = PEPServiceHelper.buildRequest("user2", "/tests/node2", "update");
            String request44 = PEPServiceHelper.buildRequest("user2", "/tests/node2", "delete");
            String request45 = PEPServiceHelper.buildRequest("user2", "/tests/node2", "owner");

            String response11 = getBeanToTest().query(request11);
            String response12 = getBeanToTest().query(request12);
            String response13 = getBeanToTest().query(request13);
            String response14 = getBeanToTest().query(request14);
            String response15 = getBeanToTest().query(request15);

            String response21 = getBeanToTest().query(request21);
            String response22 = getBeanToTest().query(request22);
            String response23 = getBeanToTest().query(request23);
            String response24 = getBeanToTest().query(request24);
            String response25 = getBeanToTest().query(request25);

            String response31 = getBeanToTest().query(request31);
            String response32 = getBeanToTest().query(request32);
            String response33 = getBeanToTest().query(request33);
            String response34 = getBeanToTest().query(request34);
            String response35 = getBeanToTest().query(request35);

            String response41 = getBeanToTest().query(request41);
            String response42 = getBeanToTest().query(request42);
            String response43 = getBeanToTest().query(request43);
            String response44 = getBeanToTest().query(request44);
            String response45 = getBeanToTest().query(request45);

            assertTrue(response11.indexOf("Permit") != -1);
            assertTrue(response12.indexOf("Deny") != -1);
            assertTrue(response13.indexOf("Deny") != -1);
            assertTrue(response14.indexOf("Deny") != -1);
            assertTrue(response15.indexOf("Deny") != -1);

            assertTrue(response21.indexOf("Permit") != -1);
            assertTrue(response22.indexOf("Permit") != -1);
            assertTrue(response23.indexOf("Permit") != -1);
            assertTrue(response24.indexOf("Permit") != -1);
            assertTrue(response25.indexOf("Permit") != -1);

            assertTrue(response31.indexOf("Permit") != -1);
            assertTrue(response32.indexOf("Permit") != -1);
            assertTrue(response33.indexOf("Permit") != -1);
            assertTrue(response34.indexOf("Permit") != -1);
            assertTrue(response35.indexOf("Permit") != -1);

            assertTrue(response41.indexOf("Permit") != -1);
            assertTrue(response42.indexOf("Deny") != -1);
            assertTrue(response43.indexOf("Deny") != -1);
            assertTrue(response44.indexOf("Deny") != -1);
            assertTrue(response45.indexOf("Deny") != -1);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testRequest8() {
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream("policies/policy-04.xml");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            getBeanToTest().getPolicyRepositoryService().getPolicyRepository().addPolicy("policy04",baos.toByteArray());

            is = ClassLoader.getSystemResourceAsStream("policies/policy-05.xml");
            baos = new ByteArrayOutputStream();
            buffer = new byte[1024];
            nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            getBeanToTest().getPolicyRepositoryService().getPolicyRepository().addPolicy("policy05",baos.toByteArray());

            is = ClassLoader.getSystemResourceAsStream("policies/policy-06.xml");
            baos = new ByteArrayOutputStream();
            buffer = new byte[1024];
            nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            getBeanToTest().getPolicyRepositoryService().getPolicyRepository().addPolicy("policy06",baos.toByteArray());

            is = ClassLoader.getSystemResourceAsStream("policies/policy-07.xml");
            baos = new ByteArrayOutputStream();
            buffer = new byte[1024];
            nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            getBeanToTest().getPolicyRepositoryService().getPolicyRepository().addPolicy("policy07",baos.toByteArray());

            String request11 = PEPServiceHelper.buildRequest("root", "/tests/node1", "owner");
            String request12 = PEPServiceHelper.buildRequest("root", "/tests/node2", "delete");
            String request13 = PEPServiceHelper.buildRequest("root", "/tests/nodenotexists", "create");

            String request21 = PEPServiceHelper.buildRequest("guest", "/tests/node1", "read");
            String request22 = PEPServiceHelper.buildRequest("guest", "/tests/node2", "read");
            String request23 = PEPServiceHelper.buildRequest("guest", "/tests/nodenotexists", "read");
            String request24 = PEPServiceHelper.buildRequest("guest", "/tests/node1", "create");
            String request25 = PEPServiceHelper.buildRequest("guest", "/tests/node2", "create");
            String request26 = PEPServiceHelper.buildRequest("guest", "/tests/nodenotexists", "create");

            String response11 = getBeanToTest().query(request11);
            String response12 = getBeanToTest().query(request12);
            String response13 = getBeanToTest().query(request13);

            String response21 = getBeanToTest().query(request21);
            String response22 = getBeanToTest().query(request22);
            String response23 = getBeanToTest().query(request23);
            String response24 = getBeanToTest().query(request24);
            String response25 = getBeanToTest().query(request25);
            String response26 = getBeanToTest().query(request26);

            assertTrue(response11.indexOf("Permit") != -1);
            assertTrue(response12.indexOf("Permit") != -1);
            assertTrue(response13.indexOf("Permit") != -1);

            assertTrue(response21.indexOf("Permit") != -1);
            assertTrue(response22.indexOf("Permit") != -1);
            assertTrue(response23.indexOf("Permit") != -1);
            assertTrue(response24.indexOf("Deny") != -1);
            assertTrue(response25.indexOf("Deny") != -1);
            assertTrue(response26.indexOf("Deny") != -1);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testRequest9() {
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream("policies/policy-08.xml");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            getBeanToTest().getPolicyRepositoryService().getPolicyRepository().addPolicy("policy08",baos.toByteArray());

            is = ClassLoader.getSystemResourceAsStream("policies/policy-06.xml");
            baos = new ByteArrayOutputStream();
            buffer = new byte[1024];
            nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            getBeanToTest().getPolicyRepositoryService().getPolicyRepository().addPolicy("policy06",baos.toByteArray());

            String request11 = PEPServiceHelper.buildRequest("root", "/", "create");
            String request12 = PEPServiceHelper.buildRequest("root", "/tests", "create");
            String request13 = PEPServiceHelper.buildRequest("root", "/tests/node1", "create");

            String request21 = PEPServiceHelper.buildRequest("guest", "/", "read");
            String request22 = PEPServiceHelper.buildRequest("guest", "/tests", "read");
            String request23 = PEPServiceHelper.buildRequest("guest", "/tests/node1", "read");
            String request24 = PEPServiceHelper.buildRequest("guest", "/tests/node1", "create");

            String request31 = PEPServiceHelper.buildRequest("user2", "/", "create");
            String request32 = PEPServiceHelper.buildRequest("user2", "/tests/node1", "create");

            String response11 = getBeanToTest().query(request11);
            String response12 = getBeanToTest().query(request12);
            String response13 = getBeanToTest().query(request13);

            String response21 = getBeanToTest().query(request21);
            String response22 = getBeanToTest().query(request22);
            String response23 = getBeanToTest().query(request23);
            String response24 = getBeanToTest().query(request24);

            String response31 = getBeanToTest().query(request31);
            String response32 = getBeanToTest().query(request32);

            assertTrue(response11.indexOf("Permit") != -1);
            assertTrue(response12.indexOf("Permit") != -1);
            assertTrue(response13.indexOf("Permit") != -1);

            assertTrue(response21.indexOf("Permit") != -1);
            assertTrue(response22.indexOf("Permit") != -1);
            assertTrue(response23.indexOf("Permit") != -1);
            assertTrue(response24.indexOf("Deny") != -1);

            assertTrue(response31.indexOf("Deny") != -1);
            assertTrue(response32.indexOf("Permit") != -1);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testRequest10() {
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream("policies/policy-09.xml");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            getBeanToTest().getPolicyRepositoryService().getPolicyRepository().addPolicy("policy09",baos.toByteArray());

            String request11 = PEPServiceHelper.buildRequest("somebody", "/", "create");
            String request12 = PEPServiceHelper.buildRequest("somebody", "/", "exists");
            String request13 = PEPServiceHelper.buildRequest("somebody", "/tests/node1", "create");
            String request14 = PEPServiceHelper.buildRequest("somebody", "/tests/node1", "exists");
            String request15 = PEPServiceHelper.buildRequest("somebodyelse", "/tests/node1", "exists");

            String response11 = getBeanToTest().query(request11);
            String response12 = getBeanToTest().query(request12);
            String response13 = getBeanToTest().query(request13);
            String response14 = getBeanToTest().query(request14);
            String response15 = getBeanToTest().query(request15);

            assertTrue(response11.indexOf("Deny") != -1);
            assertTrue(response12.indexOf("Permit") != -1);
            assertTrue(response13.indexOf("Deny") != -1);
            assertTrue(response14.indexOf("Permit") != -1);
            assertTrue(response15.indexOf("Permit") != -1);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testRequest11() {
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream("policies/policy-10.xml");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            getBeanToTest().getPolicyRepositoryService().getPolicyRepository().addPolicy("policy10",baos.toByteArray());

            String request11 = PEPServiceHelper.buildRequest("somebody", "/", "create");
            String request12 = PEPServiceHelper.buildRequest("somebody", "/", "read");
            String request13 = PEPServiceHelper.buildRequest("somebody", "/", "read-node");
            String request14 = PEPServiceHelper.buildRequest("somebody", "/testnodebidon", "read-node");
            String request15 = PEPServiceHelper.buildRequest("otherone", "/testnodebidon", "read-node");
            String request16 = PEPServiceHelper.buildRequest("otherone", "/testnodebidon", "read-the-fucking-manual");

            String response11 = getBeanToTest().query(request11);
            String response12 = getBeanToTest().query(request12);
            String response13 = getBeanToTest().query(request13);
            String response14 = getBeanToTest().query(request14);
            String response15 = getBeanToTest().query(request15);
            String response16 = getBeanToTest().query(request16);

            assertTrue(response11.indexOf("Deny") != -1);
            assertTrue(response12.indexOf("Permit") != -1);
            assertTrue(response13.indexOf("Permit") != -1);
            assertTrue(response14.indexOf("Permit") != -1);
            assertTrue(response15.indexOf("Permit") != -1);
            assertTrue(response16.indexOf("Permit") != -1);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testRequest12() {
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream("policies/policy-11.xml");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            getBeanToTest().getPolicyRepositoryService().getPolicyRepository().addPolicy("policy11",baos.toByteArray());

            String request11 = PEPServiceHelper.buildRequest("somebody", "/", "exists");

            String request21 = PEPServiceHelper.buildRequest("somebody", "/tests/node1", "exists");
            String request22 = PEPServiceHelper.buildRequest("somebody", "/tests/node1", "read");
            String request23 = PEPServiceHelper.buildRequest("somebody", "/tests/node1", "read-node");

            String request31 = PEPServiceHelper.buildRequest("somebody", "/tests2/node1", "exists");
            String request32 = PEPServiceHelper.buildRequest("somebody", "/tests2/node1", "read");
            String request33 = PEPServiceHelper.buildRequest("somebody", "/tests2/node1", "read-node");

            String request41 = PEPServiceHelper.buildRequest("somebody", "/tests/node2", "exists");
            String request42 = PEPServiceHelper.buildRequest("somebody", "/tests/node2", "read");
            String request43 = PEPServiceHelper.buildRequest("somebody", "/tests/node2", "read-node");

            String request51 = PEPServiceHelper.buildRequest("somebody", "/tests/node1/node11", "exists");
            String request52 = PEPServiceHelper.buildRequest("somebody", "/tests/node1/node11", "read");
            String request53 = PEPServiceHelper.buildRequest("somebody", "/tests/node1/node11", "read-node");

            String response11 = getBeanToTest().query(request11);
            String response21 = getBeanToTest().query(request21);
            String response22 = getBeanToTest().query(request22);
            String response23 = getBeanToTest().query(request23);
            String response31 = getBeanToTest().query(request31);
            String response32 = getBeanToTest().query(request32);
            String response33 = getBeanToTest().query(request33);
            String response41 = getBeanToTest().query(request41);
            String response42 = getBeanToTest().query(request42);
            String response43 = getBeanToTest().query(request43);
            String response51 = getBeanToTest().query(request51);
            String response52 = getBeanToTest().query(request52);
            String response53 = getBeanToTest().query(request53);

            assertTrue(response11.indexOf("NotApplicable") != -1);

            assertTrue(response21.indexOf("Permit") != -1);
            assertTrue(response22.indexOf("Permit") != -1);
            assertTrue(response23.indexOf("Permit") != -1);

            assertTrue(response31.indexOf("NotApplicable") != -1);
            assertTrue(response32.indexOf("NotApplicable") != -1);
            assertTrue(response33.indexOf("NotApplicable") != -1);

            assertTrue(response41.indexOf("Permit") != -1);
            assertTrue(response42.indexOf("Permit") != -1);
            assertTrue(response43.indexOf("Permit") != -1);

            assertTrue(response51.indexOf("Permit") != -1);
            assertTrue(response52.indexOf("Permit") != -1);
            assertTrue(response53.indexOf("Permit") != -1);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
