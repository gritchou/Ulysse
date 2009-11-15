package org.qualipso.factory.eventqueue.entity;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.eventqueue.EventQueueService;
import org.qualipso.factory.eventqueue.entity.Event;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 11 june 2009
 */
@Entity
@XmlType(name = EventQueue.RESOURCE_NAME, namespace =
    FactoryNamingConvention.RESOURCE_NAMESPACE + EventQueue.RESOURCE_NAME, propOrder = {"events"}
 )
public class EventQueue extends FactoryResource {
    
    public static final String RESOURCE_NAME = "eventqueue";

    private static final long serialVersionUID = 8866543643223847878L;
    
    @Id
    private String name;
    private String path;

    private ArrayList<Event> events;

    @XmlElement(name = "events", required = true)
    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public EventQueue() {
    }

    @XmlAttribute(name = "name", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name = "path", required = true)
    @Transient
    @Override
    public String getResourcePath() {
        return path;
    }

    public void setResourcePath(String path) {
        this.path = path;
    }

    @Override
    @XmlTransient
    public FactoryResourceIdentifier getFactoryResourceIdentifier() {
        return new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueue.RESOURCE_NAME, getName());
    }

    @Override
    @XmlTransient
    public String getResourceName() {
        return name;
    }

}
