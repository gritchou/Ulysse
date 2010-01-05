package org.qualipso.factory.jabuti;




/**
 * Description of the class CoverageDetails.
 *
 *
 */
public class CoverageDetails {

	private String name;
	private CoverageCriterionDetails[] criteria;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public CoverageCriterionDetails[] getCriteria() {
		return criteria;
	}
	public void setCriteria(CoverageCriterionDetails[] criteria) {
		this.criteria = criteria;
	}



}