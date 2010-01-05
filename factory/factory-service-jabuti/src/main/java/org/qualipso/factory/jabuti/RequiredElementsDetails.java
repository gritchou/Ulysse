package org.qualipso.factory.jabuti;


/**
 * Description of the class RequiredElementsDetails.
 *
 *
 */
public class RequiredElementsDetails {

	private String methodName;
	private WsCriterion[] criterion;
	
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public WsCriterion[] getCriterion() {
		return criterion;
	}
	public void setCriterion(WsCriterion[] criterion) {
		this.criterion = criterion;
	}
}