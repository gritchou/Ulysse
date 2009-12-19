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
package org.qualipso.factory.ssh.session;

import org.apache.mina.core.session.IoSession;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.session.AbstractSession;
import org.apache.sshd.server.SessionFactory;


/**
 * A factory for creating SSHServerSession instead of sshd internal server session.
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 24 September 2009
 */
public class SSHSessionFactory extends SessionFactory {
    protected SshServer server;

    public void setServer(SshServer server) {
        this.server = server;
    }

    protected AbstractSession createSession(IoSession ioSession)
        throws Exception {
        return new SSHSession(server, ioSession);
    }
}
