package org.qualipso.factory.core.entity;

import java.sql.Blob;
import java.sql.SQLException;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 19 august 2009
 */
@Entity
@XmlType(name = "File", namespace = "http://org.qualipso.factory.ws/resource/file", propOrder =  {
    "name", "description", "size", "contentType", "nbReads"})
@SuppressWarnings("serial")
public class File extends FactoryResource {
	
	private static Log logger = LogFactory.getLog(File.class);
	
	@Id
    private String id;
	private String path;
    private String name;
    private String description;
    private double size;
    private String contentType;
    private long nbReads;
    private Blob blob;

    @XmlAttribute(name = "id", required = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @XmlElement(name = "name", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "description", required = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name = "size", required = true)
    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    @XmlElement(name = "content-type", required = true)
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @XmlElement(name = "number-of-reads", required = true)
    public long getNbReads() {
        return nbReads;
    }

    public void setNbReads(long nbReads) {
        this.nbReads = nbReads;
    }

	@Lob 
	@Basic(fetch = FetchType.LAZY)
	@XmlTransient
	public Blob getBlob() {
		return blob;
	}
	
	public void setBlob(Blob blob) {
		try {
			logger.debug("setting blob for file with id : " + id + ", blob length : " + blob.length());
		} catch (SQLException e) {
			logger.warn("unable to get blob informations" + e);
		}
		this.blob = blob;
	}
	
	@Override
	@XmlTransient
	public FactoryResourceIdentifier getFactoryResourceIdentifier() {
		return new FactoryResourceIdentifier("CoreService", "File", getId());
	}

	@Override
	@XmlTransient
	public String getResourceName() {
		return getName();
	}
	
}
