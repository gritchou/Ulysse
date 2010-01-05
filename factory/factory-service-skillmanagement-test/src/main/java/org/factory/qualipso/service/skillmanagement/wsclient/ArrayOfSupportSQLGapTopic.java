
package org.factory.qualipso.service.skillmanagement.wsclient;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfSupportSQLGapTopic complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfSupportSQLGapTopic">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SupportSQLGapTopic" type="{http://tempuri.org/}SupportSQLGapTopic" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfSupportSQLGapTopic", propOrder = {
    "supportSQLGapTopic"
})
public class ArrayOfSupportSQLGapTopic {

    @XmlElement(name = "SupportSQLGapTopic", nillable = true)
    protected List<SupportSQLGapTopic> supportSQLGapTopic;

    /**
     * Gets the value of the supportSQLGapTopic property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supportSQLGapTopic property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupportSQLGapTopic().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SupportSQLGapTopic }
     * 
     * 
     */
    public List<SupportSQLGapTopic> getSupportSQLGapTopic() {
        if (supportSQLGapTopic == null) {
            supportSQLGapTopic = new ArrayList<SupportSQLGapTopic>();
        }
        return this.supportSQLGapTopic;
    }

}
