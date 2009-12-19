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

import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.server.ShellFactory;
import org.qualipso.factory.ssh.SSHServiceException;


/**
 * A factory for creating SSHShell and allow sshd to handle shell client requests.
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 21 September 2009
 */
public class SSHShellFactory implements ShellFactory {
    private static Log logger = LogFactory.getLog(SSHShellFactory.class);
    private List<String> commands;
    
    public SSHShellFactory() {
    	commands = new Vector<String>();
    }

    @Override
    public Shell createShell() {
        logger.debug("creating a new shell");

        return new SSHShell(commands);
    }
    
    public void importCommands(String packageName) throws SSHServiceException {
        if (commands.contains(packageName)) {
            throw new SSHServiceException("A package with name " + packageName + " is already imported");
        }

        commands.add(packageName);
    }
}
