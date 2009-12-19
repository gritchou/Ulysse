package org.qualipso.factory.ssh.shell;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EchoInputStream extends FilterInputStream {

	private static final Log logger = LogFactory.getLog(EchoInputStream.class);

	private OutputStream echo;

	public EchoInputStream(InputStream in, OutputStream echo) {
		super(in);
		this.echo = echo;
	}

	@Override
	public int read() throws IOException {
		int c = super.read();
		logger.debug("byte read : " + c);
		echo.write(c);
		if ( c == 13 ) {
			echo.write(10);
		}
		echo.flush();
		return c;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		b[off] = (byte) read();
		return 1;
	}

	@Override
	public int read(byte[] b) throws IOException {
		b[0] = (byte) read();
		return 1;
	}
}
