package org.qualipso.factory.workflow.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;

/**
 * @author Emmanuel Rias (emmanuel.rias@bull.net)
 * @date 1 july 2009
 */
@Entity
@XmlType(name = Bonita.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE + Bonita.RESOURCE_NAME, 
		propOrder = { "instanceUUID" , "activityInstanceUUID", "taskUUID",
		"task", "initiator", "dateOfCreation", "taskState", "projectValidated"})
@Table(name = "`BONITA`")
@SuppressWarnings("serial")
public class Bonita extends FactoryResource {
	public static final String RESOURCE_NAME = "bonita";
	@Id
	private String id;
	private String path;

	private String instanceUUID;
	private String activityInstanceUUID;
	private String taskUUID;
	private String task;
	private String initiator;
	private String dateOfCreation;
	private String taskState;
	private boolean projectValidated;


	public Bonita(){
	}

	public void setInstanceUUID(String instance) {
		this.instanceUUID = instance;
	}
	@XmlElement(name = "instance", required = true)
	public String getInstanceUUID() {
		return instanceUUID;
	}
	
	public void setActivityInstanceUUID(String activityInstanceUUID) {
		this.activityInstanceUUID = activityInstanceUUID;
	}

	@XmlElement(name = "activityInstanceUUID", required = true)
	public String getActivityInstanceUUID() {
		return activityInstanceUUID;
	}
	
	public void setTaskUUID(String taskUUID) {
		this.taskUUID = taskUUID;
	}

	@XmlElement(name = "taskUUID", required = true)
	public String getTaskUUID() {
		return taskUUID;
	}
	
	public void setTask(String task) {
		this.task = task;		
	}
	
	@XmlElement(name = "task", required = true)
	public String getTask() {
		return task;
	}
	
	public void setInitiator(String initiator) {
		this.initiator = initiator;		
	}
	@XmlElement(name = "initiator", required = true)	
	public String getInitiator() {
		return initiator;
	}
	
	public void setTaskState(String taskState) {
		this.taskState = taskState;		
	}

	@XmlElement(name = "taskState", required = true)	
	public String getTaskState() {
		return taskState;
	}
	
	public void setDateOfCreation(String dateOfCreation) {
		this.dateOfCreation = dateOfCreation;		
	}

	@XmlElement(name = "dateOfCreation", required = true)	
	public String getDateOfCreation() {
		return dateOfCreation;
	}

	@Override
	@XmlTransient
	public FactoryResourceIdentifier getFactoryResourceIdentifier() {
		return new FactoryResourceIdentifier("WorkflowService",
				"BonitaInstance", getId());
	}

	@Override
	@XmlTransient
	public String getResourceName() {
		return getId();
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


	@XmlElement(name = "projectValidated", required = true)	
	public boolean isProjectValidated() {
		return projectValidated;
	}

	public void setProjectValidated(boolean projectValidated) {
		this.projectValidated = projectValidated;
	}


}
