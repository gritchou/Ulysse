package org.qualipso.factory.subversion.entity;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;

/**
 * @author Emmanuel Rias (emmanuel.rias@bull.net)
 * @date 1 july 2009
 */
@Entity
@XmlType(name = "SVNLogEntry", namespace = "http://org.qualipso.factory.ws/resource/svnlogentry", propOrder = {
		"author", "date", "message", "revision", "changedPaths" })
@SuppressWarnings("serial")
public class SVNLogEntry extends FactoryResource {

	@Id
	private String id;
	private String author;
	private ArrayList<SVNLogEntryPath> changedPaths;
	private Date date;
	private String message;
	private long revision;

	private String path;

	public SVNLogEntry() {
	}

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

	@Override
	@XmlTransient
	public FactoryResourceIdentifier getFactoryResourceIdentifier() {
		return new FactoryResourceIdentifier("SubversionService",
				"SVNLogEntry", getId());
	}

	@Override
	@XmlTransient
	public String getResourceName() {
		return getId();
	}

	@XmlElement(name = "author", required = false)
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@XmlElement(name = "changedPaths", required = false)
	public ArrayList<SVNLogEntryPath> getChangedPaths() {
		return changedPaths;
	}

	public void setChangedPaths(ArrayList<SVNLogEntryPath> changedPaths) {
		this.changedPaths = changedPaths;
	}

	@XmlElement(name = "date", required = false)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@XmlElement(name = "message", required = false)
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@XmlElement(name = "revision", required = false)
	public long getRevision() {
		return revision;
	}

	public void setRevision(long revision) {
		this.revision = revision;
	}

}
