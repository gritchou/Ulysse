
package org.qualipso.factory.client.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for bonita complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="bonita">
 *   &lt;complexContent>
 *     &lt;extension base="{http://org.qualipso.factory.ws/service/workflow}factoryResource">
 *       &lt;sequence>
 *         &lt;element name="instance" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="activityInstanceUUID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="taskUUID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="task" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="initiator" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dateOfCreation" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="taskState" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="projectValidated" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="path" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "bonita", namespace = "http://org.qualipso.factory.ws/resource/bonita", propOrder = {
    "instance",
    "activityInstanceUUID",
    "taskUUID",
    "task",
    "initiator",
    "dateOfCreation",
    "taskState",
    "projectValidated"
})
public class Bonita
    extends FactoryResource
{

    @XmlElement(required = true)
    protected String instance;
    @XmlElement(required = true)
    protected String activityInstanceUUID;
    @XmlElement(required = true)
    protected String taskUUID;
    @XmlElement(required = true)
    protected String task;
    @XmlElement(required = true)
    protected String initiator;
    @XmlElement(required = true)
    protected String dateOfCreation;
    @XmlElement(required = true)
    protected String taskState;
    protected boolean projectValidated;
    @XmlAttribute(required = true)
    protected String id;
    @XmlAttribute(required = true)
    protected String path;

    /**
     * Gets the value of the instance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstance() {
        return instance;
    }

    /**
     * Sets the value of the instance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstance(String value) {
        this.instance = value;
    }

    /**
     * Gets the value of the activityInstanceUUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActivityInstanceUUID() {
        return activityInstanceUUID;
    }

    /**
     * Sets the value of the activityInstanceUUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActivityInstanceUUID(String value) {
        this.activityInstanceUUID = value;
    }

    /**
     * Gets the value of the taskUUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaskUUID() {
        return taskUUID;
    }

    /**
     * Sets the value of the taskUUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaskUUID(String value) {
        this.taskUUID = value;
    }

    /**
     * Gets the value of the task property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTask() {
        return task;
    }

    /**
     * Sets the value of the task property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTask(String value) {
        this.task = value;
    }

    /**
     * Gets the value of the initiator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInitiator() {
        return initiator;
    }

    /**
     * Sets the value of the initiator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInitiator(String value) {
        this.initiator = value;
    }

    /**
     * Gets the value of the dateOfCreation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateOfCreation() {
        return dateOfCreation;
    }

    /**
     * Sets the value of the dateOfCreation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateOfCreation(String value) {
        this.dateOfCreation = value;
    }

    /**
     * Gets the value of the taskState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaskState() {
        return taskState;
    }

    /**
     * Sets the value of the taskState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaskState(String value) {
        this.taskState = value;
    }

    /**
     * Gets the value of the projectValidated property.
     * 
     */
    public boolean isProjectValidated() {
        return projectValidated;
    }

    /**
     * Sets the value of the projectValidated property.
     * 
     */
    public void setProjectValidated(boolean value) {
        this.projectValidated = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the path property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the value of the path property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPath(String value) {
        this.path = value;
    }

}
