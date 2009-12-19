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
package org.qualipso.factory.ssh.command;

import org.apache.sshd.server.CommandFactory.Command;


/**
 * Abstract class for all SSH commands allowing to provide command args. This class is tipically used after command lien parsing
 * to dissociate command name and args.
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 24 September 2009
 */
public abstract class SSHCommand implements Command {
    private String[] args;

    public SSHCommand() {
        this.args = new String[0];
    }

    public SSHCommand(String[] args) {
        this.args = args;
    }

    public String[] getArgs() {
        return args;
    }

    public String getArg(int pos) {
        return args[pos];
    }
}
