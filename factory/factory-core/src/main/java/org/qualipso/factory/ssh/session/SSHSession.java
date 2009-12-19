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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.mina.core.session.IoSession;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.future.CloseFuture;
import org.apache.sshd.server.session.ServerSession;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;


/**
 * A specific server session to store the JAAS login context. This allow to logout before session close.
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 24 September 2009
 */
public class SSHSession extends ServerSession {
    private static Log logger = LogFactory.getLog(SSHSession.class);
    private LoginContext lc;

    public SSHSession(SshServer server, IoSession ioSession)
        throws Exception {
        super(server, ioSession);
    }

    public void setLoginContext(LoginContext lc) {
        this.lc = lc;
    }

    @Override
    public CloseFuture close(boolean immediately) {
        logger.debug("closing ssh server session");

        try {
            lc.logout();
        } catch (LoginException e) {
            logger.error("unable to logout ", e);
        }

        return super.close(immediately);
    }
}
