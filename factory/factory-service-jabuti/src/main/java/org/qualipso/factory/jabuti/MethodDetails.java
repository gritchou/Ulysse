package org.qualipso.factory.jabuti;





/**
 * Description of the class MethodDetails.
 *
 *
 */
public class MethodDetails {

	private String methodName;
	private CriterionCoveredUncovered[] criteria;
	
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public CriterionCoveredUncovered[] getCriteria() {
		return criteria;
	}
	public void setCriteria(CriterionCoveredUncovered[] criteria) {
		this.criteria = criteria;
	}
}