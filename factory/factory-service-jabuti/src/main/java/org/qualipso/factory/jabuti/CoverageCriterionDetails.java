package org.qualipso.factory.jabuti;




/**
 * Description of the class CoverageCriterionDetails.
 *
 *
 */
public class CoverageCriterionDetails {

	private String criterionName;
	private int numberOfElements;
	private int numberOfCoveredElements;
	private float coveragePercentage;
	
	public String getCriterionName() {
		return criterionName;
	}
	public void setCriterionName(String criterionName) {
		this.criterionName = criterionName;
	}
	public int getNumberOfElements() {
		return numberOfElements;
	}
	public void setNumberOfElements(int numberOfElements) {
		this.numberOfElements = numberOfElements;
	}
	public int getNumberOfCoveredElements() {
		return numberOfCoveredElements;
	}
	public void setNumberOfCoveredElements(int numberOfCoveredElements) {
		this.numberOfCoveredElements = numberOfCoveredElements;
	}
	public float getCoveragePercentage() {
		return coveragePercentage;
	}
	public void setCoveragePercentage(float coveragePercentage) {
		this.coveragePercentage = coveragePercentage;
	}

}