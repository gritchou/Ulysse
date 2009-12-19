package org.qualipso.factory.collaboration.beans;

import java.io.Serializable;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryNamingConvention;

@XmlType(name = ParticipantDetails.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE
	+ ParticipantDetails.RESOURCE_NAME, propOrder = { "path","profile","decision"})
public class ParticipantDetails implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String RESOURCE_NAME = "participants";
    
    private String path;
    private String profile;
    private String decision = "pending";

    @XmlAttribute(name = "path", required = true)
    @Transient
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @XmlElement(name = "profile", required = true)
    @Transient
    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    @XmlElement(name = "decision", required = true)
    @Transient
    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer("Invitation. Event: "+this.path);
	sb.append(" profile: "+this.profile +" decision "+this.decision);
	return sb.toString();
    }

}
