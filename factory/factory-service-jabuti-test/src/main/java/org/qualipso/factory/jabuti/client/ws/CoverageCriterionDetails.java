
package org.qualipso.factory.jabuti.client.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for coverageCriterionDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="coverageCriterionDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="coveragePercentage" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="criterionName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="numberOfCoveredElements" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="numberOfElements" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "coverageCriterionDetails", propOrder = {
    "coveragePercentage",
    "criterionName",
    "numberOfCoveredElements",
    "numberOfElements"
})
public class CoverageCriterionDetails {

    protected float coveragePercentage;
    protected String criterionName;
    protected int numberOfCoveredElements;
    protected int numberOfElements;

    /**
     * Gets the value of the coveragePercentage property.
     * 
     */
    public float getCoveragePercentage() {
        return coveragePercentage;
    }

    /**
     * Sets the value of the coveragePercentage property.
     * 
     */
    public void setCoveragePercentage(float value) {
        this.coveragePercentage = value;
    }

    /**
     * Gets the value of the criterionName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCriterionName() {
        return criterionName;
    }

    /**
     * Sets the value of the criterionName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCriterionName(String value) {
        this.criterionName = value;
    }

    /**
     * Gets the value of the numberOfCoveredElements property.
     * 
     */
    public int getNumberOfCoveredElements() {
        return numberOfCoveredElements;
    }

    /**
     * Sets the value of the numberOfCoveredElements property.
     * 
     */
    public void setNumberOfCoveredElements(int value) {
        this.numberOfCoveredElements = value;
    }

    /**
     * Gets the value of the numberOfElements property.
     * 
     */
    public int getNumberOfElements() {
        return numberOfElements;
    }

    /**
     * Sets the value of the numberOfElements property.
     * 
     */
    public void setNumberOfElements(int value) {
        this.numberOfElements = value;
    }

}
