
package org.qualipso.factory.jabuti.client.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for instrumentedProjectDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="instrumentedProjectDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="commandLine" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="file" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "instrumentedProjectDetails", propOrder = {
    "commandLine",
    "file"
})
public class InstrumentedProjectDetails {

    protected String commandLine;
    protected byte[] file;

    /**
     * Gets the value of the commandLine property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommandLine() {
        return commandLine;
    }

    /**
     * Sets the value of the commandLine property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommandLine(String value) {
        this.commandLine = value;
    }

    /**
     * Gets the value of the file property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getFile() {
        return file;
    }

    /**
     * Sets the value of the file property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setFile(byte[] value) {
        this.file = ((byte[]) value);
    }

}
