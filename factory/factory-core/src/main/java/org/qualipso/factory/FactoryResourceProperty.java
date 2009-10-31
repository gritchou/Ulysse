package org.qualipso.factory;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 18 June 2009
 */
@XmlType(name = "FactoryResourceProperty", namespace = "http://org.qualipso.factory.ws/factory")
@SuppressWarnings("serial")
public class FactoryResourceProperty implements Serializable {
	
	public static final String CREATION_TIMESTAMP = "creation-date";
	public static final String LAST_UPDATE_TIMESTAMP = "last-update-date";
	public static final String AUTHOR = "author";
	public static final String OWNER = "owner";
	public static final String POLICY_ID = "policy-id";
	
	
	private String name;
    private String value;

    public FactoryResourceProperty() {
    }
    
    public FactoryResourceProperty(String name, String value) {
    	this.value = value;
    	this.name = name;
    }
    
    @XmlAttribute(name = "name", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name = "value", required = true)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FactoryResourceProperty other = (FactoryResourceProperty) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}
    
}
