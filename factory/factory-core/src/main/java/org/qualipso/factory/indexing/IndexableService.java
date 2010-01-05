package org.qualipso.factory.indexing;

import javax.ejb.Local;

import org.qualipso.factory.FactoryException;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.binding.PathNotFoundException;

@Local
public interface IndexableService {
	
	/**
     * Get the indexable content related to the data of the resource located at a given path.
     * 
     * @param path the path to the resource.
     * @return the IndexableContent corresponding to the content to add into index.
     * @throws InvalidPathException if the path is invalid
     * @throws PathNotFoundException if the path doesn't point to any resource
     * @throws FactoryException
     */
    public abstract IndexableContent getIndexableContent(String path)  throws FactoryException, InvalidPathException, PathNotFoundException;

}
