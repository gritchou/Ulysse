package org.qualipso.factory.utils;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryNamingConvention;

@XmlType(name = ListItem.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE
		+ ListItem.RESOURCE_NAME, propOrder = { "data" })
public class ListItem {
	
	public static final String RESOURCE_NAME = "list-item";

    protected String data;
    
   public ListItem(String data) {
	   this.data = data;
   }
   
   @Transient
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}