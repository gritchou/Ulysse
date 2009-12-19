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


/**
 * A DummyPassword Authenticator to bypass JAAS authentication mechanism and allow testing or ease development.<br/>
 * <br/>
 * NOT TO USE IN PRODUCTION
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 21 September 2009
 */
public class DummyPasswordAuthenticator implements PasswordAuthenticator {
    private static Log logger = LogFactory.getLog(DummyPasswordAuthenticator.class);

    @Override
    public Object authenticate(String username, String password, ServerSession session) {
        logger.debug("authentication for user " + username + " password " + password);

        return username;
    }
}
