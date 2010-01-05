
package org.factory.qualipso.service.skillmanagement.wsclient.org.tempuri;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfSupportSQLUserComp complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfSupportSQLUserComp">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SupportSQLUserComp" type="{http://tempuri.org/}SupportSQLUserComp" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfSupportSQLUserComp", propOrder = {
    "supportSQLUserComp"
})
public class ArrayOfSupportSQLUserComp {

    @XmlElement(name = "SupportSQLUserComp", nillable = true)
    protected List<SupportSQLUserComp> supportSQLUserComp;

    /**
     * Gets the value of the supportSQLUserComp property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supportSQLUserComp property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupportSQLUserComp().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SupportSQLUserComp }
     * 
     * 
     */
    public List<SupportSQLUserComp> getSupportSQLUserComp() {
        if (supportSQLUserComp == null) {
            supportSQLUserComp = new ArrayList<SupportSQLUserComp>();
        }
        return this.supportSQLUserComp;
    }

}
