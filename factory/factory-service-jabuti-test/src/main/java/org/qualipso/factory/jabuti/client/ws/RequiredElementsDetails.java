
package org.qualipso.factory.jabuti.client.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for requiredElementsDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="requiredElementsDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="criterion" type="{http://org.qualipso.factory.ws/service/jabuti}wsCriterion" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="methodName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "requiredElementsDetails", propOrder = {
    "criterion",
    "methodName"
})
public class RequiredElementsDetails {

    @XmlElement(nillable = true)
    protected List<WsCriterion> criterion;
    protected String methodName;

    /**
     * Gets the value of the criterion property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the criterion property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCriterion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WsCriterion }
     * 
     * 
     */
    public List<WsCriterion> getCriterion() {
        if (criterion == null) {
            criterion = new ArrayList<WsCriterion>();
        }
        return this.criterion;
    }

    /**
     * Gets the value of the methodName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Sets the value of the methodName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMethodName(String value) {
        this.methodName = value;
    }

}
