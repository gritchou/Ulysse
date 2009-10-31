package org.qualipso.factory.security.repository;

import java.util.List;
import java.util.Map;

import com.sun.xacml.EvaluationCtx;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 28 august 2009
 */
public abstract class PolicyRepository {
	
	public void init() throws PolicyRepositoryException {
	}
	
	public void shutdown() throws PolicyRepositoryException {
	}
	
	abstract public void addPolicy(String id, byte[] policy) throws PolicyRepositoryException;

	abstract public void deletePolicy(String id) throws PolicyRepositoryException;

	abstract public void updatePolicy(String id, byte[] policy) throws PolicyRepositoryException;

	abstract public byte[] getPolicy(String id) throws PolicyRepositoryException;

	abstract public List<String> listPolicies() throws PolicyRepositoryException;
	
	abstract public Map<String, byte[]> getPolicies(EvaluationCtx eval) throws PolicyRepositoryException;
}
