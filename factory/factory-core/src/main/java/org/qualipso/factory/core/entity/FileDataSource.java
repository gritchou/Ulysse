package org.qualipso.factory.core.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import javax.activation.DataSource;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 19 august 2009
 */
public class FileDataSource implements DataSource {

	private File file;
	
	public FileDataSource(File file) {
		this.file = file;
	}
	
	@Override
	public String getContentType() {
		return file.getContentType();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		try {
			return file.getBlob().getBinaryStream();
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		try {
			return file.getBlob().setBinaryStream(1);
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

}
