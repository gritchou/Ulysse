
package org.factory.qualipso.service.skillmanagement.wsclient;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfSupportSQLTop complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfSupportSQLTop">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SupportSQLTop" type="{http://tempuri.org/}SupportSQLTop" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfSupportSQLTop", propOrder = {
    "supportSQLTop"
})
public class ArrayOfSupportSQLTop {

    @XmlElement(name = "SupportSQLTop", nillable = true)
    protected List<SupportSQLTop> supportSQLTop;

    /**
     * Gets the value of the supportSQLTop property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supportSQLTop property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupportSQLTop().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SupportSQLTop }
     * 
     * 
     */
    public List<SupportSQLTop> getSupportSQLTop() {
        if (supportSQLTop == null) {
            supportSQLTop = new ArrayList<SupportSQLTop>();
        }
        return this.supportSQLTop;
    }

}
