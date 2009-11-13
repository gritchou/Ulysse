
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
 *         &lt;element name="SearchGapTopicResult" type="{http://tempuri.org/}ArrayOfSupportSQLGapTopic" minOccurs="0"/>
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
    "searchGapTopicResult"
})
@XmlRootElement(name = "SearchGapTopicResponse")
public class SearchGapTopicResponse {

    @XmlElement(name = "SearchGapTopicResult")
    protected ArrayOfSupportSQLGapTopic searchGapTopicResult;

    /**
     * Gets the value of the searchGapTopicResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSupportSQLGapTopic }
     *     
     */
    public ArrayOfSupportSQLGapTopic getSearchGapTopicResult() {
        return searchGapTopicResult;
    }

    /**
     * Sets the value of the searchGapTopicResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSupportSQLGapTopic }
     *     
     */
    public void setSearchGapTopicResult(ArrayOfSupportSQLGapTopic value) {
        this.searchGapTopicResult = value;
    }

}
