package org.qualipso.factory.jabuti;




/**
 * Description of the class CriterionCoveredUncovered.
 *
 *
 */
public class CriterionCoveredUncovered {

	private String name;
	private String[] coveredElements;
	private String[] uncoveredElements;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String[] getCoveredElements() {
		return coveredElements;
	}
	public void setCoveredElements(String[] coveredElements) {
		this.coveredElements = coveredElements;
	}
	public String[] getUncoveredElements() {
		return uncoveredElements;
	}
	public void setUncoveredElements(String[] uncoveredElements) {
		this.uncoveredElements = uncoveredElements;
	}



}