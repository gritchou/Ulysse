package org.qualipso.factory.security.repository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.xacml.EvaluationCtx;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 27 august 2009
 */
//TODO include management like a XA resource to allow Transactions.
//TODO pakcage this as a JBoss Service.
public class FilePolicyRepository extends PolicyRepository {
	
	private static final String DEFAULT_REPOSITORY_FOLDER = "data/policy-repository";
	private static Log logger = LogFactory.getLog(FilePolicyRepository.class);
	private File repositoryFolder;

	public FilePolicyRepository() {
		this(new File(DEFAULT_REPOSITORY_FOLDER));
	}
	
	public FilePolicyRepository(File repositoryFolder) {
		this.repositoryFolder = repositoryFolder;
		logger.info("repository created with folder : " + this.repositoryFolder.getAbsolutePath());
	}
	
	public void init() throws PolicyRepositoryException {
		if (!repositoryFolder.exists()) {
			repositoryFolder.mkdirs();
		}
		logger.info("repository initialized");
	}
	
	public File getRepositoryFolder() {
		return repositoryFolder;
	}

	@Override
	public void addPolicy(String id, byte[] policy) throws PolicyRepositoryException {
		try {
			File policyFile = new File(repositoryFolder, id);
			FileOutputStream fos = new FileOutputStream(policyFile);
			fos.write(policy);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			throw new PolicyRepositoryException("unable to add policy", e);
		}
	}

	@Override
	public void deletePolicy(String id) throws PolicyRepositoryException {
		File policyFile = new File(repositoryFolder, id);

		if (!policyFile.exists()) {
			throw new PolicyRepositoryException("unable to delete policy : no policy found for id " + id);
		} else {
			policyFile.delete();
			logger.debug("policy deleted for id " + id);
		}
	}

	@Override
	public byte[] getPolicy(String id) throws PolicyRepositoryException {
		try {
			File policyFile = new File(repositoryFolder, id);

			if (!policyFile.exists()) {
				throw new PolicyRepositoryException("unable to get policy : no policy found for id " + id);
			} else {
				FileInputStream fis = new FileInputStream(policyFile);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int bytesRead = 0;

				while ((bytesRead = fis.read(buffer)) != -1) {
					baos.write(buffer, 0, bytesRead);
				}

				baos.flush();
				baos.close();

				return baos.toByteArray();
			}
		} catch (Exception e) {
			throw new PolicyRepositoryException("unable to get policy", e);
		}
	}

	@Override
	public List<String> listPolicies() throws PolicyRepositoryException {
		File[] policyFiles = repositoryFolder.listFiles();
		Vector<String> policies = new Vector<String>();

		for (int i = 0; i < policyFiles.length; i++) {
			policies.add(policyFiles[i].getName());
		}

		return policies;
	}

	@Override
	public void updatePolicy(String id, byte[] policy) throws PolicyRepositoryException {
		File policyFile = new File(repositoryFolder, id);

		if (!policyFile.exists()) {
			throw new PolicyRepositoryException("unable to update policy : no policy found for id " + id);
		} else {
			try {
				policyFile.delete();
				policyFile.createNewFile();

				FileOutputStream fos = new FileOutputStream(policyFile);
				fos.write(policy);
				fos.flush();
				fos.close();
				logger.debug("policy updated for id " + id);
			} catch (Exception e) {
				throw new PolicyRepositoryException("unable to update policy", e);
			}
		}
	}

	@Override
	public Map<String, byte[]> getPolicies(EvaluationCtx eval) throws PolicyRepositoryException {
		try {
			List<String> policiesId = listFiles();

			Map<String, byte[]> policies = new Hashtable<String, byte[]>();

			// For better performances, perform a prefiltering of policies before evaluation should be better.
			for (Iterator<String> iter = policiesId.iterator(); iter.hasNext();) {
				String id = iter.next();
				policies.put(id, loadFile(id));
			}

			return policies;
		} catch (PolicyRepositoryException pse) {
			throw new PolicyRepositoryException("unable to read policies from the source", pse);
		}
	}

	private List<String> listFiles() {
		File[] policyFiles = repositoryFolder.listFiles();
		Vector<String> policies = new Vector<String>();

		for (int i = 0; i < policyFiles.length; i++) {
			policies.add(policyFiles[i].getName());
		}

		return policies;
	}

	private byte[] loadFile(String filename) throws PolicyRepositoryException {
		try {
			File policyFile = new File(repositoryFolder, filename);

			if (!policyFile.exists()) {
				throw new PolicyRepositoryException("unable to load policy file : no file found with name " + filename);
			} else {
				FileInputStream fis = new FileInputStream(policyFile);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int bytesRead = 0;

				while ((bytesRead = fis.read(buffer)) != -1) {
					baos.write(buffer, 0, bytesRead);
				}

				baos.flush();
				baos.close();

				return baos.toByteArray();
			}
		} catch (Exception e) {
			throw new PolicyRepositoryException("unable to load policy file", e);
		}
	}

}
