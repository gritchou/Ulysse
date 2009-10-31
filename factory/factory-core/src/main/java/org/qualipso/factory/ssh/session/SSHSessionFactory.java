package org.qualipso.factory.ssh.session;

import org.apache.mina.core.session.IoSession;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.session.AbstractSession;
import org.apache.sshd.server.SessionFactory;

public class SSHSessionFactory extends SessionFactory {

    protected SshServer server;

    public void setServer(SshServer server) {
        this.server = server;
    }

    protected AbstractSession createSession(IoSession ioSession) throws Exception {
        return new SSHServerSession(server, ioSession);
    }

}