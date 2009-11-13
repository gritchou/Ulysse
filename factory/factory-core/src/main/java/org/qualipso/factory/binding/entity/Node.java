package org.qualipso.factory.binding.entity;

import java.io.Serializable;
import java.util.HashMap;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * The Node class is the representation of a path segment. 
 * 
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 18 June 2009
 */
@Entity
@SuppressWarnings("serial")
public class Node implements Serializable {
    @Id
    private String id;
    private String parent;
    private String pathPart;
    private String bindedResourceIdentifier;
    private HashMap<String, String> children;
    private HashMap<String, String> properties;
    
    public Node() {
    	properties = new HashMap<String, String>(1);
        children = new HashMap<String, String>(1);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPathPart() {
        return pathPart;
    }

    public void setPathPart(String pathPart) {
        this.pathPart = pathPart;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getBindedResourceIdentifier() {
        return bindedResourceIdentifier;
    }

    public void setBindedResourceIdentifier(String resoureceIdentifier) {
        this.bindedResourceIdentifier = resoureceIdentifier;
    }

    public HashMap<String, String> getChildren() {
        return children;
    }

    public void setChildren(HashMap<String, String> children) {
        this.children = children;
    }

    public void addChild(String pathPart, String id) {
    	children.put(pathPart, id);
    }

    public String getChild(String pathPart) {
        return children.get(pathPart);
    }
    
    public void removeChild(String pathPart) {
    	children.remove(pathPart);
    }
    
    public HashMap<String, String> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, String> properties) {
        this.properties = properties;
    }

    public void setProperty(String name, String value) {
        properties.put(name, value);
    }

    public String getProperty(String name) {
        return properties.get(name);
    }
    
    public String toString() {
    	return "{id:" + id + "} {parent:" + parent + "} {binded:" + bindedResourceIdentifier + "} {path-part:" + pathPart + "}";
    }
}
