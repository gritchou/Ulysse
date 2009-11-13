package org.qualipso.factory.svn.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.SshServer;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.qualipso.factory.svn.ssh.command.SVNServeSSHCommand;
import org.qualipso.factory.svn.utils.SubversionResources;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * @author Gerald Oster (oster@loria.fr)
 * @date 28 October 2009
 */
public class SVNServeCommandTest {

	private static final Log logger = LogFactory.getLog(SVNServeCommandTest.class);

	private static final String SSHD_HOSTNAME = "localhost";
	private static final int SSHD_PORT = 8022;
	private static final String USERNAME = "anonymous";
	private static final String PASSWD = "mysecret";
	private static final String REPO_TEST = "test-svn";

	private SshServer sshd;
	private File workingDir;
	private File repositoryDir;

	@Before
	public void before() {
		SVNRepositoryFactoryImpl.setup();

		// create server svn repository
		repositoryDir = new File(new File(SubversionResources.getInstance().getRootDirRepositories(), REPO_TEST).getAbsolutePath());
		repositoryDir.mkdirs();
		try {
			SVNURL localURL = SVNRepositoryFactory.createLocalRepository(repositoryDir, true, true);
		} catch (SVNException e) {
			logger.warn("unable to create local repository for test in path '" + repositoryDir.getAbsolutePath() + "'",
					e);
			fail(e.getMessage());
		}

		// create a working directory
		workingDir = new File(new File("target/local").getAbsolutePath());
		workingDir.mkdirs();

		// start ssh server which serve svnserve command
		startSVNServe();
	}

	@After
	public void after() {
		stopSVNServe();
		deleteFolderRecursively(workingDir);
		deleteFolderRecursively(repositoryDir);
	}

	@Test
	public void testLocalImportToRepository() {
		try {
			// Create a local svn workspace
			File workFolder1 = new File(workingDir, "testdir");
			workFolder1.mkdir();

			// Create some file and commit locally
			File file11 = new File(workFolder1, "testfile1.txt");
			FileOutputStream fos11 = new FileOutputStream(file11);
			fos11.write("Youhou".getBytes());
			fos11.flush();
			fos11.close();

			ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
			ISVNAuthenticationManager authManager = new BasicAuthenticationManager(USERNAME, PASSWD);
			SVNClientManager svnClientManager = SVNClientManager.newInstance(options, authManager);

			SVNURL dstURL = SVNURL.parseURIDecoded("svn+ssh://" + USERNAME + "@" + SSHD_HOSTNAME + ":" + SSHD_PORT
					+ "/" + REPO_TEST);
			SVNCommitClient client = svnClientManager.getCommitClient();
			SVNCommitInfo infos = client.doImport(workFolder1, dstURL, "first import", null, false, false,
					SVNDepth.INFINITY);

			assertTrue("HEAD revision should be 1 but is " + infos.getNewRevision(), infos.getNewRevision() == 1);

			// logger.debug("dumping svn repository");
			// SVNAdminClient admin = svnClientManager.getAdminClient();
			// BufferedOutputStream dumpStream = new
			// BufferedOutputStream(System.out);
			// admin.doDump(repositoryDir, dumpStream, SVNRevision.create(0),
			// SVNRevision.HEAD, true, true);
			// dumpStream.flush();
			// dumpStream.close();

		} catch (Exception e) {
			logger.error(e);
			fail(e.getMessage());
		}
	}

	private void deleteFolderRecursively(File folder) {
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				deleteFolderRecursively(file);
			} else {
				file.delete();
			}
		}
		folder.delete();
	}

	public void startSVNServe() {
		sshd = SshServer.setUpDefaultServer();
		sshd.setPort(SSHD_PORT);

		sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
			public Object authenticate(String username, String password, ServerSession session) {
				if (USERNAME.equals(username) && PASSWD.equals(password))
					return true;
				else
					return false;
			}
		});
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("target/tmp/hostkey.ser"));

		sshd.setCommandFactory(new CommandFactory() {
			@Override
			public Command createCommand(String command) {
				logger.debug("creating command for '" + command + "'");
				return new SVNServeSSHCommand(new String[] { repositoryDir.toString() });
			}
		});

		try {
			sshd.start();
			logger.debug("server started.");
		} catch (IOException e) {
			logger.error("error while starting ssh daemon", e);
		}

	}

	public void stopSVNServe() {
		if (sshd != null) {
			sshd.stop();
			sshd = null;
		}
		logger.debug("server stopped.");
	}
}
