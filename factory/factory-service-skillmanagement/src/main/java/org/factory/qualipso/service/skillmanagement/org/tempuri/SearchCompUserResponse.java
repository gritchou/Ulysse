
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
 *         &lt;element name="SearchCompUserResult" type="{http://tempuri.org/}ArrayOfSupportSQLUserComp" minOccurs="0"/>
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
    "searchCompUserResult"
})
@XmlRootElement(name = "SearchCompUserResponse")
public class SearchCompUserResponse {

    @XmlElement(name = "SearchCompUserResult")
    protected ArrayOfSupportSQLUserComp searchCompUserResult;

    /**
     * Gets the value of the searchCompUserResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSupportSQLUserComp }
     *     
     */
    public ArrayOfSupportSQLUserComp getSearchCompUserResult() {
        return searchCompUserResult;
    }

    /**
     * Sets the value of the searchCompUserResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSupportSQLUserComp }
     *     
     */
    public void setSearchCompUserResult(ArrayOfSupportSQLUserComp value) {
        this.searchCompUserResult = value;
    }

}
