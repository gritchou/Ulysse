
package org.factory.qualipso.service.skillmanagement.org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SupportSQLUserComp complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SupportSQLUserComp">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id_competence" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="User" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Result" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Count_User" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Topic_Number" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Satisfied_Levels" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SupportSQLUserComp", propOrder = {
    "idCompetence",
    "user",
    "result",
    "countUser",
    "topicNumber",
    "satisfiedLevels"
})
public class SupportSQLUserComp {

    @XmlElement(name = "id_competence")
    protected int idCompetence;
    @XmlElement(name = "User")
    protected String user;
    @XmlElement(name = "Result")
    protected String result;
    @XmlElement(name = "Count_User")
    protected int countUser;
    @XmlElement(name = "Topic_Number")
    protected int topicNumber;
    @XmlElement(name = "Satisfied_Levels")
    protected int satisfiedLevels;

    /**
     * Gets the value of the idCompetence property.
     * 
     */
    public int getIdCompetence() {
        return idCompetence;
    }

    /**
     * Sets the value of the idCompetence property.
     * 
     */
    public void setIdCompetence(int value) {
        this.idCompetence = value;
    }

    /**
     * Gets the value of the user property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the value of the user property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUser(String value) {
        this.user = value;
    }

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResult(String value) {
        this.result = value;
    }

    /**
     * Gets the value of the countUser property.
     * 
     */
    public int getCountUser() {
        return countUser;
    }

    /**
     * Sets the value of the countUser property.
     * 
     */
    public void setCountUser(int value) {
        this.countUser = value;
    }

    /**
     * Gets the value of the topicNumber property.
     * 
     */
    public int getTopicNumber() {
        return topicNumber;
    }

    /**
     * Sets the value of the topicNumber property.
     * 
     */
    public void setTopicNumber(int value) {
        this.topicNumber = value;
    }

    /**
     * Gets the value of the satisfiedLevels property.
     * 
     */
    public int getSatisfiedLevels() {
        return satisfiedLevels;
    }

    /**
     * Sets the value of the satisfiedLevels property.
     * 
     */
    public void setSatisfiedLevels(int value) {
        this.satisfiedLevels = value;
    }

}
