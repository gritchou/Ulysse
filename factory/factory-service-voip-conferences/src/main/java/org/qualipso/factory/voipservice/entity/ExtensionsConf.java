package org.qualipso.factory.voipservice.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Index;

/**
 * @author <a href="mailto:janny@man.poznan.pl">Dariusz Janny</a>
 * @author <a href="mailto:debe@man.poznan.pl">Marcin Wrzos</a>
 * @company Poznan Supercomputing and Networking Center
 * @license LGPL
 * @project QualiPSo 
 * @date 24/07/2009
 */

@Entity
@Table(name = "extensions_conf", uniqueConstraints={@UniqueConstraint(columnNames={"context","exten","priority"})})
public class ExtensionsConf implements Serializable{
	private static final long serialVersionUID = -1607664039221846285L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false, length = 40)
	private String context="";

	@Column(nullable = false, length = 40)
	@Index(name="exten_index")
	private String exten="";

	@Column(nullable = false, length = 40)
	private String app="";

	@Column(length = 256)
	private String appdata="";

	@Column(nullable = false)
	private Integer priority=0;

	public ExtensionsConf() {}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getAppdata() {
		return appdata;
	}

	public void setAppdata(String appdata) {
		this.appdata = appdata;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getExten() {
		return exten;
	}

	public void setExten(String exten) {
		this.exten = exten;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}
}