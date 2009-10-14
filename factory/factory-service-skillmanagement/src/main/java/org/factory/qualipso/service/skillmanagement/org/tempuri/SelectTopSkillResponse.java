
package org.factory.qualipso.service.skillmanagement.org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SelectTopSkillResult" type="{http://tempuri.org/}ArrayOfSupportSQLTop" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "selectTopSkillResult"
})
@XmlRootElement(name = "SelectTopSkillResponse")
public class SelectTopSkillResponse {

    @XmlElement(name = "SelectTopSkillResult")
    protected ArrayOfSupportSQLTop selectTopSkillResult;

    /**
     * Gets the value of the selectTopSkillResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSupportSQLTop }
     *     
     */
    public ArrayOfSupportSQLTop getSelectTopSkillResult() {
        return selectTopSkillResult;
    }

    /**
     * Sets the value of the selectTopSkillResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSupportSQLTop }
     *     
     */
    public void setSelectTopSkillResult(ArrayOfSupportSQLTop value) {
        this.selectTopSkillResult = value;
    }

}
