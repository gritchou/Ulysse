
package org.factory.qualipso.service.skillmanagement.wsclient;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfSupportSQLCompTop complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfSupportSQLCompTop">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SupportSQLCompTop" type="{http://tempuri.org/}SupportSQLCompTop" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfSupportSQLCompTop", propOrder = {
    "supportSQLCompTop"
})
public class ArrayOfSupportSQLCompTop {

    @XmlElement(name = "SupportSQLCompTop", nillable = true)
    protected List<SupportSQLCompTop> supportSQLCompTop;

    /**
     * Gets the value of the supportSQLCompTop property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supportSQLCompTop property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupportSQLCompTop().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SupportSQLCompTop }
     * 
     * 
     */
    public List<SupportSQLCompTop> getSupportSQLCompTop() {
        if (supportSQLCompTop == null) {
            supportSQLCompTop = new ArrayList<SupportSQLCompTop>();
        }
        return this.supportSQLCompTop;
    }

}
