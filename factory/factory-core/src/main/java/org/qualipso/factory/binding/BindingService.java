package org.qualipso.factory.binding;

import javax.ejb.Local;

import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;

/**
 * The Binding Service provide a tree like naming scheme in which you can attach some FactoryResourceIdentifier.<br/>
 * <br/>
 * The role of this service is to bind a path (like /parent/children/leaf) of a hierarchical tree with a
 * FactoryResourceIdentifier. In fact you can attach any resource in the tree. This allows a uniform naming scheme for
 * all factory resource and a common way to access them.<br/>
 * <br/> 
 * This internal service is not visible remotely and should only be used by trusted services. <br/>
 * <br/>
 * The following rules applies when you try to bind a FactoryResourceIdentifier to a path : 
 * <ul>
 * <li>the parent path must exist,
 * <li>the given FactoryResourceIdentifier must not be binded to another path,
 * <li>the path must not be already bound,
 * <li>you cannot bind the root path (/)
 * </ul> 
 * It is also possible to set some property for a given node. Properties consist of a simple name/value pair that is attached 
 * to the node.<br/>
 * A property can be inherited. If the property is not set on the given path but on a parent, parent property value is inherited.
 * 
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 18 June 2009
 */
@Local
public interface BindingService {
	
	public static final String SERVICE_NAME = "binding";
	
	/**
	 * Bind a FactoryResourceIdentifier to the given path.<br/>
	 * - the parent path must exists<br/>
	 * - the resource identifier must not be already bound to an existing path<br/>
	 * - the path mut not be already bound
	 * 
	 * @param identifier the FactoryResourceIdentifier to bind
	 * @param path the path
	 * @throws BindingServiceException
	 * @throws InvalidPathException if the path is not valid { @see PathHelper.valid() }
	 * @throws PathNotFoundException if the parent does not exists
	 * @throws PathAlreadyBoundException if the path is already bound to another factory resource identifier
	 * @throws IdentifierAlreadyBoundException if the FactoryResourceIdentifier is already bound to another path
	 */
	public void bind(FactoryResourceIdentifier identifier, String path) throws BindingServiceException, InvalidPathException, PathNotFoundException, PathAlreadyBoundException, IdentifierAlreadyBoundException;
	
	/**
	 * Unbind a path. The path must not have any children to be unbind.
	 * 
	 * @param path the path to unbind
	 * @throws BindingServiceException 
	 * @throws InvalidPathException if the path is not valid { @see PathHelper.valid() } 
	 * @throws PathNotFoundException if the path does not exists
	 * @throws PathNotEmptyException if the path contains children
	 */
	public void unbind(String path) throws BindingServiceException, InvalidPathException, PathNotFoundException, PathNotEmptyException;
	
	/**
	 * Rebind a path to another resource identifier.
	 * 
	 * @param identifier the new FactoryResourceIdentifier to bind.
	 * @param path the path to rebind
	 * @throws BindingServiceException 
	 * @throws InvalidPathException if the path is not valid { @see PathHelper.valid() } 
	 * @throws PathNotFoundException if the path does not exists
	 * @throws IdentifierAlreadyBoundException if the FactoryResourceIdentifier is already bound to another path
	 */
	public void rebind(FactoryResourceIdentifier identifier, String path) throws BindingServiceException, InvalidPathException, PathNotFoundException, IdentifierAlreadyBoundException;
	
	/**
	 * List the children path of a given path.
	 * 
	 * @param path the parent path to list
	 * @return a String array containing the names of all path children.
	 * @throws InvalidPathException if the path is not valid { @see PathHelper.valid() } 
	 * @throws PathNotFoundException if the path does not exists
	 */
	public String[] list(String path) throws InvalidPathException, PathNotFoundException;
	
	/**
	 * Lookup the resource identifier binded to the given path.
	 * 
	 * @param path the path to lookup
	 * @return the FactoryResourceIdentifier
	 * @throws InvalidPathException if the path is not valid { @see PathHelper.valid() } 
	 * @throws PathNotFoundException if the path does not exists
	 */
	public FactoryResourceIdentifier lookup(String path) throws InvalidPathException, PathNotFoundException;
	
	/**
	 * Set a property on the given node. If the property name is already define for this node, value is replaced.
	 * 
	 * @param path the path for the property
	 * @param name the name of the property
	 * @param value the value of the property
	 * @throws BindingServiceException
	 * @throws InvalidPathException if the path is not valid { @see PathHelper.valid() } 
	 * @throws PathNotFoundException if the path does not exists
	 */
	public void setProperty(String path, String name, String value) throws BindingServiceException, InvalidPathException, PathNotFoundException;
	
	/**
	 * Retreive a property set on a given path.
	 * 
	 * @param path the path of the property
	 * @param name the name of the property 
	 * @param inherited if true, try to find the property 
	 * @return the value of the property
	 * @throws InvalidPathException if the path is not valid { @see PathHelper.valid() } 
	 * @throws PathNotFoundException if the path does not exists
	 * @throws PropertyNotFoundException if the property does not exists for this node nor in a parent if inherited is set to true
	 */
	public String getProperty(String path, String name, boolean inherited) throws InvalidPathException, PathNotFoundException, PropertyNotFoundException;
	
	/**
	 * Retreive all availables properties for a given path. 
	 * 
	 * @param path the path you want retreive properties on 
	 * @param inherited if true inherit all parents properties.
	 * @return an array with all properties found
	 * @throws InvalidPathException if the path is not valid { @see PathHelper.valid() } 
	 * @throws PathNotFoundException if the path does not exists
	 */
	public FactoryResourceProperty[] getProperties(String path, boolean inherited) throws InvalidPathException, PathNotFoundException;
	
}
