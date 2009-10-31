package org.qualipso.factory.git;

import java.io.InputStream;
import java.io.OutputStream;

import javax.ejb.Local;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 28 september 2009
 */
@Local
public interface GITServiceLocal {
	
	public void pushToGITRepository(String path, InputStream in, OutputStream out, OutputStream messages) throws GITServiceException;
	
	public void pullFromGITRepository(String path, InputStream in, OutputStream out, OutputStream messages) throws GITServiceException;

}
