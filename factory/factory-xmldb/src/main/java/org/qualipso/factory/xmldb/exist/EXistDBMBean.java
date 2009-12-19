package org.qualipso.factory.xmldb.exist;

import org.jboss.system.ServiceMBean;

/**
 * 
 * @author Jerome Blanchard (jayblanc@gmail.com) 
 * @date 11 november 2009
 */
public interface EXistDBMBean extends ServiceMBean {
	
	public String getStatus();
    public String getConfiguration();
    public String getDataDirectory();
    public String getActiveInstances();
    public String getAvailableInstances();
    public String getEXistHome(); 
    public void setEXistHome (String existHome);
    
}
