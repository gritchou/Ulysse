package org.qualipso.factory.eventqueue.entity;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Nicolas HENRY
 * @author Marlène HANTZ
 */
@Entity
@XmlType(name = "Rule", namespace = "http://org.qualipso.factory.ws/entity", propOrder = { "id", "subjectre", "objectre", "targetre", "queuePath" })
public class Rule implements Serializable {

    private static final long serialVersionUID = -3537424425785767451L;
    private static Log logger = LogFactory.getLog(Rule.class);
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

    /**
     * Tells if the event in parameter matches the rule
     * 
     * @param e
     *            the event to match
     * @return
     */

    public boolean match(Event e) {
        logger.info("match(...) called");
        logger.debug("subject " + subjectre + " " + e.getThrowedBy());
        Pattern p1 = Pattern.compile(subjectre);
        Matcher m1 = p1.matcher(e.getThrowedBy());

        Pattern p2 = Pattern.compile(objectre);
        Matcher m2 = p2.matcher(e.getEventType());
        logger.debug("objectre " + objectre + " " + e.getEventType());

        Pattern p3 = Pattern.compile(targetre);
        Matcher m3 = p3.matcher(e.getFromResource());
        logger.debug("targetre " + targetre + " " + e.getFromResource());

        logger.debug("m1 " + m1.matches() + " m2 " + m2.matches() + " m3 " + m3.matches());
        return m1.matches() && m2.matches() && m3.matches();
    }

    @Override
    public String toString() {
        return "Rule :\nsubjectre = \"" + subjectre + "\"\nobjectre = \"" + objectre + "\"\ntargetre = \"" + targetre + "\"\nqueuePath = \"" + queuePath + "\"";
    }

}
