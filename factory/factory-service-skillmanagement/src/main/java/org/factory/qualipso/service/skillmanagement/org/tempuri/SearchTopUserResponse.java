
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
 *         &lt;element name="SearchTopUserResult" type="{http://tempuri.org/}ArrayOfSupportSQLUser" minOccurs="0"/>
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
    "searchTopUserResult"
})
@XmlRootElement(name = "SearchTopUserResponse")
public class SearchTopUserResponse {

    @XmlElement(name = "SearchTopUserResult")
    protected ArrayOfSupportSQLUser searchTopUserResult;

    /**
     * Gets the value of the searchTopUserResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSupportSQLUser }
     *     
     */
    public ArrayOfSupportSQLUser getSearchTopUserResult() {
        return searchTopUserResult;
    }

    /**
     * Sets the value of the searchTopUserResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSupportSQLUser }
     *     
     */
    public void setSearchTopUserResult(ArrayOfSupportSQLUser value) {
        this.searchTopUserResult = value;
    }

}
