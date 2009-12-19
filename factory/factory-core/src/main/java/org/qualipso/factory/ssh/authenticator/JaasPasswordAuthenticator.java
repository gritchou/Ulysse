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
package org.qualipso.factory.ssh.authenticator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import org.jboss.security.auth.callback.UsernamePasswordHandler;

import org.qualipso.factory.ssh.session.SSHSession;

import javax.security.auth.login.LoginContext;


/**
 * A JAAS Password Authenticator to integrate with existing system authentication.<br/>
 * <br/>
 * A hint is used. First JAAS try is using JBossWSDigest server side authentication login config in order to get an error
 * immediately in case of wrong credentials because client-login stands for first service call. In the SSH client this produce
 * a strange effect that let you authenticate but stand for first command to send you authentication exception.
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 21 September 2009
 */
public class JaasPasswordAuthenticator implements PasswordAuthenticator {
    private static Log logger = LogFactory.getLog(JaasPasswordAuthenticator.class);

    @Override
    public Object authenticate(final String username, final String password, final ServerSession session) {
        logger.debug("trying to authenticate user {" + username + "}");

        try {
            //Trying to authenticate throw the ServerSide LoginModule stack to check if account is valid.
            LoginContext lc = new LoginContext("JBossWSDigest", new UsernamePasswordHandler(username, password));
            lc.login();
            logger.debug("valid login");
            lc.logout();
        } catch (Exception e) {
            logger.debug("invalid login and/or password");

            return null;
        }

        try {
            //Now that login/password are valid, performing a client login to ensure Security Association process.
            LoginContext lc = new LoginContext("client-login", new UsernamePasswordHandler(username, password));
            lc.login();

            if (session instanceof SSHSession) {
                ((SSHSession) session).setLoginContext(lc);
            } else {
                logger.warn("ServerSession is not of type SSHServerSession : unable to set LoginContext for futur logout");
            }

            logger.debug("user logged");

            return username;
        } catch (Exception e) {
            logger.debug("login failed");

            return null;
        }
    }
}
