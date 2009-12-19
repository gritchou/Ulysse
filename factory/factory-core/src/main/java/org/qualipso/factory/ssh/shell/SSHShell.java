/*
 *
 * Qualipso Factory
 * Copyright (C) 2006-2010 INRIA
 * http://www.inria.fr - molli@loria.fr
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of LGPL. See licenses details in LGPL.txt
 *
 * Initial authors :
 *
 * Jérôme Blanchard / INRIA
 * Pascal Molli / Nancy Université
 * Gérald Oster / Nancy Université
 * Christophe Bouthier / INRIA
 * 
 */
package org.qualipso.factory.ssh.shell;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.server.ShellFactory.Environment;
import org.apache.sshd.server.ShellFactory.ExitCallback;
import org.apache.sshd.server.ShellFactory.SessionAware;
import org.apache.sshd.server.ShellFactory.Shell;
import org.apache.sshd.server.session.ServerSession;

import bsh.EvalError;
import bsh.Interpreter;


/**
 * A specific SSH Shell instance allowing to connect to the facotry using any ssh client.<br/>
 * <br/>
 * Stream should be redirected to a command interpreter or any kind of shell.<br/>
 * <br/>
 * IMPLEMENTATION IS NOT COMPLETE.
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 6 october 2009
 */
public class SSHShell implements Shell, SessionAware {
    private static Log logger = LogFactory.getLog(SSHShell.class);
    private InputStream in;
    private OutputStream out;
    private OutputStream err;
    private ExitCallback callback;
    private Environment env;
    private ServerSession session;
    private List<String> commands;
    private Interpreter interpreter;
    
    public SSHShell(List<String> commands) {
    	super();
    	this.commands = commands;
    }

    @Override
    public void setInputStream(InputStream in) {
        logger.info("setting input stream");
        this.in = in;
    }

    @Override
    public void setOutputStream(OutputStream out) {
        logger.info("setting output stream");
        this.out = out;
    }

    @Override
    public void setErrorStream(OutputStream err) {
        logger.info("setting error stream");
        this.err = err;
    }

    @Override
    public void setExitCallback(ExitCallback callback) {
        logger.info("setting exit callback");
        this.callback = callback;
    }

    @Override
    public void setSession(ServerSession session) {
        logger.info("setting server session");
        this.session = session;
    }

    public Environment getEnv() {
        return env;
    }

    @Override
	public void start(Environment env) throws IOException {
		logger.debug("Starting shell");
		
		interpreter = new Interpreter(new InputStreamReader(new EchoInputStream(in, out)), new PrintStream(out), new PrintStream(err), true);
		Field[] fields = interpreter.getClass().getFields();
		for (Field field : fields) {
			System.out.println(field);
		}
		Method[] methods = interpreter.getClass().getDeclaredMethods();
		for (Method method : methods) {
			System.out.println(method);
		}
		System.out.println(interpreter.getClass().getProtectionDomain().getCodeSource().getLocation());
		//interpreter.setShowResults(true);
		
		this.env = env;
		for ( String key : env.getEnv().keySet() ) {
			try {
				logger.debug("Setting interpreter variable : " + key + "=" + env.getEnv().get(key));
				interpreter.set(key, env.getEnv().get(key));
				for (String packageName : commands) {
					interpreter.eval( "importCommands(\"" + packageName + "\");" );
				}
				logger.debug("Setting interpreter prompt");
				interpreter.eval( "bsh.cwd = \"/\"" );
				interpreter.eval( "getBshPrompt() { return \"\\r\\n" + session.getUsername() + "@" + InetAddress.getLocalHost().getHostName() + ":\"; }");
			} catch (EvalError e) {
				logger.error(e.getMessage(), e);
			}
		}
		new Thread("Shell-Interpreter") {
			@Override
			public void run() {
				runImpl();
			}
		}.start();
		
	}

    private void runImpl() {
		try {
			try {
				logger.info("Starting Interpreter");

				interpreter.run();
				
				logger.info("Interpreter stopped, bye");
			} catch (Exception e) {
				logger.error("Error Executing Interpreter", e);
			}
			logger.info("Interpreter completed normally");
		} finally {
			try {
				out.flush();
			} catch (IOException err) {
				logger.error("Error running impl", err);
			}
			try {
				err.flush();
			} catch (IOException err) {
				logger.error("Error running impl", err);
			}
			callback.onExit(0);
		}

	}

	@Override
	public void destroy() {
		logger.debug("Shell Destroyed");
	}
}
