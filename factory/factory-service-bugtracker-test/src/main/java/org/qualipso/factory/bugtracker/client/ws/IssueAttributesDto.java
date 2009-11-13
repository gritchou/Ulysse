
package org.qualipso.factory.bugtracker.client.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for issueAttributesDto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="issueAttributesDto">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="priorities" type="{http://org.qualipso.factory.ws/service/bugtracker}confDataDto" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="resolutions" type="{http://org.qualipso.factory.ws/service/bugtracker}confDataDto" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="severities" type="{http://org.qualipso.factory.ws/service/bugtracker}confDataDto" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="status" type="{http://org.qualipso.factory.ws/service/bugtracker}confDataDto" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "issueAttributesDto", propOrder = {
    "priorities",
    "resolutions",
    "severities",
    "status"
})
public class IssueAttributesDto {

    @XmlElement(nillable = true)
    protected List<ConfDataDto> priorities;
    @XmlElement(nillable = true)
    protected List<ConfDataDto> resolutions;
    @XmlElement(nillable = true)
    protected List<ConfDataDto> severities;
    @XmlElement(nillable = true)
    protected List<ConfDataDto> status;

    /**
     * Gets the value of the priorities property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the priorities property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPriorities().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConfDataDto }
     * 
     * 
     */
    public List<ConfDataDto> getPriorities() {
        if (priorities == null) {
            priorities = new ArrayList<ConfDataDto>();
        }
        return this.priorities;
    }

    /**
     * Gets the value of the resolutions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resolutions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResolutions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConfDataDto }
     * 
     * 
     */
    public List<ConfDataDto> getResolutions() {
        if (resolutions == null) {
            resolutions = new ArrayList<ConfDataDto>();
        }
        return this.resolutions;
    }

    /**
     * Gets the value of the severities property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the severities property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSeverities().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConfDataDto }
     * 
     * 
     */
    public List<ConfDataDto> getSeverities() {
        if (severities == null) {
            severities = new ArrayList<ConfDataDto>();
        }
        return this.severities;
    }

    /**
     * Gets the value of the status property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the status property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStatus().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConfDataDto }
     * 
     * 
     */
    public List<ConfDataDto> getStatus() {
        if (status == null) {
            status = new ArrayList<ConfDataDto>();
        }
        return this.status;
    }

}
