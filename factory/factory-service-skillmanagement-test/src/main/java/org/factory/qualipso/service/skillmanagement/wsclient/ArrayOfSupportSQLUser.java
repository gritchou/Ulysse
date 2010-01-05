
package org.factory.qualipso.service.skillmanagement.wsclient;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfSupportSQLUser complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfSupportSQLUser">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SupportSQLUser" type="{http://tempuri.org/}SupportSQLUser" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfSupportSQLUser", propOrder = {
    "supportSQLUser"
})
public class ArrayOfSupportSQLUser {

    @XmlElement(name = "SupportSQLUser", nillable = true)
    protected List<SupportSQLUser> supportSQLUser;

    /**
     * Gets the value of the supportSQLUser property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supportSQLUser property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupportSQLUser().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SupportSQLUser }
     * 
     * 
     */
    public List<SupportSQLUser> getSupportSQLUser() {
        if (supportSQLUser == null) {
            supportSQLUser = new ArrayList<SupportSQLUser>();
        }
        return this.supportSQLUser;
    }

}
