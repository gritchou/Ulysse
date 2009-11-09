package org.qualipso.factory.notification.entity;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.eventqueue.entity.Event;

@Entity
@XmlType(name = "Rule", namespace = "http://org.qualipso.factory.ws/entity", propOrder = { "id", "subjectre", "objectre", "targetre", "queuePath" })
public class Rule implements Serializable {

    private static final long serialVersionUID = -3537424425785767451L;

    @Id
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String subjectre;
    private String objectre;
    private String targetre;
    private String queuePath;

    public String getSubjectre() {
        return subjectre;
    }

    public void setSubjectre(String subjectre) {
        this.subjectre = subjectre;
    }

    public String getObjectre() {
        return objectre;
    }

    public void setObjectre(String objectre) {
        this.objectre = objectre;
    }

    public String getTargetre() {
        return targetre;
    }

    public void setTargetre(String targetre) {
        this.targetre = targetre;
    }

    public String getQueuePath() {
        return queuePath;
    }

    public void setQueuePath(String queuePath) {
        this.queuePath = queuePath;
    }

    public boolean match(Event e) {
        Pattern p1 = Pattern.compile(subjectre);
        Matcher m1 = p1.matcher(e.getThrowedBy());
        Pattern p2 = Pattern.compile(objectre);
        Matcher m2 = p2.matcher(e.getFromResource());
        Pattern p3 = Pattern.compile(targetre);
        Matcher m3 = p3.matcher(e.getEventType());
        return m1.matches() && m2.matches() && m3.matches();
    }

}
