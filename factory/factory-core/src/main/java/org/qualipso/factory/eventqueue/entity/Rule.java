package org.qualipso.factory.eventqueue.entity;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.qualipso.factory.notification.Event;

/**
 * @author Nicolas HENRY
 * @author Marlène HANTZ
 * @author Philippe SCHMUCKER
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
     * Tells if subject matches the subject regular expression of the rule
     * 
     * @param subject
     *            subject to match
     * @return true if the subject matches the subject regular expression
     */
    public boolean matchBySubjectRE(String subject) {
        logger.info("matchBySubjectRE(...) called");
        Pattern p = Pattern.compile(subjectre);
        Matcher m = p.matcher(subject);
        return m.matches();
    }

    /**
     * Tells if object matches the object regular expression of the rule
     * 
     * @param object
     *            object to match
     * @return true if the object matches the object regular expression
     */
    public boolean matchByObjectRE(String object) {
        logger.info("matchByObjectRE(...) called");
        Pattern p = Pattern.compile(objectre);
        Matcher m = p.matcher(object);
        return m.matches();
    }

    /**
     * Tells if target matches the target regular expression of the rule
     * 
     * @param target
     *            target to match
     * @return true if the target matches the target regular expression
     */
    public boolean matchByTargetRE(String target) {
        logger.info("matchByTargetRE(...) called");
        Pattern p = Pattern.compile(targetre);
        Matcher m = p.matcher(target);
        return m.matches();
    }

    /**
     * Tells if the event in parameter matches the rule
     * 
     * @param e
     *            the event to match
     * @return true if the event matches the rule
     */

    public boolean match(Event e) {
        logger.info("match(...) called");

        boolean b1 = matchBySubjectRE(e.getThrowedBy());
        logger.debug("subject " + subjectre + " " + e.getThrowedBy());

        boolean b2 = matchByObjectRE(e.getEventType());
        logger.debug("objectre " + objectre + " " + e.getEventType());

        boolean b3 = matchByTargetRE(e.getFromResource());
        logger.debug("targetre " + targetre + " " + e.getFromResource());

        logger.debug("m1 " + b1 + " m2 " + b2 + " m3 " + b3);
        return b1 && b2 && b3;
    }

    @Override
    public String toString() {
        return "Rule :\nsubjectre = \"" + subjectre + "\"\nobjectre = \"" + objectre + "\"\ntargetre = \"" + targetre + "\"\nqueuePath = \"" + queuePath + "\"";
    }

    /**
     * Tells if queue of the rule matches the queue regular expression
     * 
     * @param queuere
     *            queue regular expression to test
     * @return true if the queue matches the queue regular expression
     */
    public boolean matchByQueue(String queuere) {
        logger.info("matchByQueue(...) called");
        Pattern p = Pattern.compile(queuere);
        Matcher m = p.matcher(queuePath);
        return m.matches();
    }

}
