package org.qualipso.factory.utils;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryNamingConvention;

@XmlType(name = ListItemArray.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE
		+ ListItem.RESOURCE_NAME, propOrder = { "item" })
public class ListItemArray {
	
	public static final String RESOURCE_NAME = "list-itemArray";

    @XmlElement(nillable = true)
    protected ArrayList<ListItem> item;

    public ArrayList<ListItem> getItem() {
        if (item == null) {
            item = new ArrayList<ListItem>();
        }
        return this.item;
    }
    
    
}
