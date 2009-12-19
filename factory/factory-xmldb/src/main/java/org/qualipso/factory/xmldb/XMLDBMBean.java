package org.qualipso.factory.xmldb;

import org.jboss.system.ServiceMBean;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;

/**
 * 
 * @author Jerome Blanchard (jayblanc@gmail.com) 
 * @date 11 november 2009
 */
public interface XMLDBMBean extends ServiceMBean {

	public String getDriver();
    public void setDriver (String driver);
    public String getBaseCollectionURI();
    public void setBaseCollectionURI(String baseCollectionURI); 
    public Collection getBaseCollection() throws XMLDBException;
    
}
