
package org.factory.qualipso.service.tempuri;

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
 *         &lt;element name="SearchTypUserResult" type="{http://tempuri.org/}ArrayOfSupportSQLUser" minOccurs="0"/>
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
    "searchTypUserResult"
})
@XmlRootElement(name = "SearchTypUserResponse")
public class SearchTypUserResponse {

    @XmlElement(name = "SearchTypUserResult")
    protected ArrayOfSupportSQLUser searchTypUserResult;

    /**
     * Gets the value of the searchTypUserResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSupportSQLUser }
     *     
     */
    public ArrayOfSupportSQLUser getSearchTypUserResult() {
        return searchTypUserResult;
    }

    /**
     * Sets the value of the searchTypUserResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSupportSQLUser }
     *     
     */
    public void setSearchTypUserResult(ArrayOfSupportSQLUser value) {
        this.searchTypUserResult = value;
    }

}
