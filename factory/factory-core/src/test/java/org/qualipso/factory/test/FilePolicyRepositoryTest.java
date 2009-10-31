package org.qualipso.factory.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.qualipso.factory.security.repository.FilePolicyRepository;
import org.qualipso.factory.security.repository.PolicyRepository;
import org.qualipso.factory.security.repository.PolicyRepositoryException;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 28 august 2009
 */
public class FilePolicyRepositoryTest  {
	
	private static Log logger = LogFactory.getLog(FilePolicyRepositoryTest.class);
	private PolicyRepository repository; 

	public FilePolicyRepositoryTest() {
		repository = new FilePolicyRepository(new File("target/data/policy-repository"));
	}

	@Before
	public void setUp() throws Exception {
		repository.init();
	}
	
	@After
	public void tearDown() {
		File file = ((FilePolicyRepository) repository).getRepositoryFolder();

        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();

            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        }

        file.delete();
	}
	
	@Test
	public void testGetPolicyError() {
		try {
			repository.getPolicy("unexistingPolicyID");
			fail("this policy should not exists");
		} catch (PolicyRepositoryException pre) {
			//
		}
    }

	@Test
    public void testAddPolicy() {
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream("policies/policy-12.xml");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            String id = UUID.randomUUID().toString(); 
            repository.addPolicy(id, baos.toByteArray());

            try {
            	repository.getPolicy(id);
            } catch (PolicyRepositoryException pse) {
                fail("unable to get the created policy : " + pse.getMessage());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

	@Test
    public void testUpdatePolicy() {
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream("policies/policy-12.xml");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            String id = UUID.randomUUID().toString(); 
            repository.addPolicy(id, baos.toByteArray());

            is = ClassLoader.getSystemResourceAsStream("policies/policy-13.xml");
            baos = new ByteArrayOutputStream();
            buffer = new byte[1024];
            nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            repository.updatePolicy(id, baos.toByteArray());

            try {
                String policy = new String(repository.getPolicy(id));
                assertTrue(policy.indexOf("policy-02") != -1);
            } catch (PolicyRepositoryException pse) {
                fail("unable to get the updated policy : " + pse.getMessage());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

	@Test
    public void testDeletePolicy() {
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream("policies/policy-12.xml");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            String id = UUID.randomUUID().toString(); 
            repository.addPolicy(id, baos.toByteArray());

            try {
            	repository.getPolicy(id);
            } catch (PolicyRepositoryException pse) {
                fail("unable to get the created policy : " + pse.getMessage());
            }

            repository.deletePolicy(id);

            try {
            	repository.getPolicy(id);
                fail("policy should not exists after deletion.");
            } catch (PolicyRepositoryException pse) {
                //
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

	@Test
    public void testListPolicies() {
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream("policies/policy-12.xml");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            String id1 = UUID.randomUUID().toString(); 
            repository.addPolicy(id1, baos.toByteArray());

            is = ClassLoader.getSystemResourceAsStream("policies/policy-13.xml");
            baos = new ByteArrayOutputStream();
            buffer = new byte[1024];
            nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            String id2 = UUID.randomUUID().toString(); 
            repository.addPolicy(id2, baos.toByteArray());

            is = ClassLoader.getSystemResourceAsStream("policies/policy-14.xml");
            baos = new ByteArrayOutputStream();
            buffer = new byte[1024];
            nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            String id3 = UUID.randomUUID().toString(); 
            repository.addPolicy(id3, baos.toByteArray());

            List<String> policies = repository.listPolicies();
            assertTrue(policies.size() >= 3);
            assertTrue(policies.contains(id1));
            assertTrue(policies.contains(id2));
            assertTrue(policies.contains(id3));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
	
	@Test
	public void testGetPolicies() {
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream("policies/policy-12.xml");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            String id1 = UUID.randomUUID().toString(); 
            repository.addPolicy(id1, baos.toByteArray());

            is = ClassLoader.getSystemResourceAsStream("policies/policy-13.xml");
            baos = new ByteArrayOutputStream();
            buffer = new byte[1024];
            nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            String id2 = UUID.randomUUID().toString(); 
            repository.addPolicy(id2, baos.toByteArray());

            is = ClassLoader.getSystemResourceAsStream("policies/policy-14.xml");
            baos = new ByteArrayOutputStream();
            buffer = new byte[1024];
            nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            String id3 = UUID.randomUUID().toString(); 
            repository.addPolicy(id3, baos.toByteArray());

            Map<String, byte[]> policies = repository.getPolicies(null);
            assertTrue(policies.size() >= 3);

            for (Iterator<String> iterator = policies.keySet().iterator(); iterator.hasNext();) {
                String key = iterator.next();
                String policy = new String(policies.get(key));
                assertTrue((policy.indexOf("policy-01") != -1) || (policy.indexOf("policy-02") != -1) || (policy.indexOf("policy-03") != -1));
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
	
}
