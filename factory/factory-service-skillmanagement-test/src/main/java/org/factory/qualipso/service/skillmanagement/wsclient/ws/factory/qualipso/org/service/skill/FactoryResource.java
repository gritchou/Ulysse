
package org.factory.qualipso.service.skillmanagement.wsclient.ws.factory.qualipso.org.service.skill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.factory.qualipso.service.skillmanagement.wsclient.ws.factory.qualipso.org.resource.file.File;
import org.factory.qualipso.service.skillmanagement.wsclient.ws.factory.qualipso.org.resource.folder.Folder;
import org.factory.qualipso.service.skillmanagement.wsclient.ws.factory.qualipso.org.resource.group.Group;
import org.factory.qualipso.service.skillmanagement.wsclient.ws.factory.qualipso.org.resource.link.Link;
import org.factory.qualipso.service.skillmanagement.wsclient.ws.factory.qualipso.org.resource.profile.Profile;


/**
 * <p>Java class for factoryResource complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="factoryResource">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "factoryResource")
@XmlSeeAlso({
    File.class,
    Folder.class,
    Group.class,
    Profile.class,
    Link.class
})
public abstract class FactoryResource {


}
