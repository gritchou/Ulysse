package org.qualipso.factory.binding;

import javax.ejb.Local;

import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 18 June 2009
 */
@Local
public interface BindingService {
	
	public void bind(FactoryResourceIdentifier identifier, String path) throws BindingServiceException, InvalidPathException, PathNotFoundException, PathAlreadyBoundException, IdentifierAlreadyBoundException;
	
	public void unbind(String path) throws BindingServiceException, InvalidPathException, PathNotFoundException;
	
	public void rebind(FactoryResourceIdentifier identifier, String path) throws BindingServiceException, InvalidPathException, PathNotFoundException, IdentifierAlreadyBoundException;
	
	public String[] list(String path) throws InvalidPathException, PathNotFoundException;
	
	public FactoryResourceIdentifier lookup(String path) throws InvalidPathException, PathNotFoundException;
	
	public void setProperty(String path, String name, String value) throws BindingServiceException, InvalidPathException, PathNotFoundException;
	
	public String getProperty(String path, String name, boolean inherited) throws InvalidPathException, PathNotFoundException, PropertyNotFoundException;
	
	public FactoryResourceProperty[] getProperties(String path, boolean inherited) throws InvalidPathException, PathNotFoundException;
	
}
