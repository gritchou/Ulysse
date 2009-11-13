package org.qualipso.factory.git.test.sb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.login.LoginContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.auth.callback.UsernamePasswordHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.bootstrap.BootstrapService;
import org.qualipso.factory.bootstrap.BootstrapServiceException;
import org.qualipso.factory.core.CoreService;
import org.qualipso.factory.git.GITService;
import org.qualipso.factory.git.entity.GITRepository;
import org.qualipso.factory.git.test.AllTests;
import org.spearce.jgit.lib.Commit;
import org.spearce.jgit.lib.Constants;
import org.spearce.jgit.lib.GitIndex;
import org.spearce.jgit.lib.ObjectId;
import org.spearce.jgit.lib.ObjectWriter;
import org.spearce.jgit.lib.PersonIdent;
import org.spearce.jgit.lib.RefUpdate;
import org.spearce.jgit.lib.Repository;
import org.spearce.jgit.lib.TextProgressMonitor;
import org.spearce.jgit.transport.FetchConnection;
import org.spearce.jgit.transport.PushConnection;
import org.spearce.jgit.transport.Transport;

public class GITServiceSBTest {
	
	private static Log logger = LogFactory.getLog(GITServiceSBTest.class);
    private static Context ctx;
    
    private GITService git;
    private CoreService core;
	private File workingDir;

	@BeforeClass
	public static void beforeClass() throws NamingException {
		try {
			logger.debug("jaas config file path : " + ClassLoader.getSystemResource("jaas.config").getPath());
			System.setProperty("java.security.auth.login.config", ClassLoader.getSystemResource("jaas.config").getPath());
		} catch (Exception e) {
			logger.error("unable to load local jaas.config file");
		}
		
		Properties properties = new Properties();
		properties.put("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
		properties.put("java.naming.factory.url.pkgs","org.jboss.naming:org.jnp.interfaces");
		properties.put("java.naming.provider.url","localhost:1099");
		ctx = new InitialContext(properties);
		
		BootstrapService bootstrap = (BootstrapService) ctx.lookup(FactoryNamingConvention.getJNDINameForService("bootstrap"));
		try {
			bootstrap.bootstrap();
		} catch (BootstrapServiceException e) {
			logger.error(e);
		}
	}
    
    @Before
    public void before() {
    	try {
    		workingDir = new File(new File("target/repos").getAbsolutePath());
    		
    		git = (GITService) ctx.lookup(FactoryNamingConvention.getJNDINameForService("git"));
			core = (CoreService) ctx.lookup(FactoryNamingConvention.getJNDINameForService("core"));
			
			LoginContext lc = new LoginContext("qualipso", new UsernamePasswordHandler("root", AllTests.ROOT_ACCOUNT_PASS));
            lc.login();
            
            core.createFolder("/testrepos", "GITRepositories", "All the git repositories");
            
            lc.logout();
    	} catch (Exception e) {
			logger.error(e);
			fail(e.getMessage());
		}
    }
    
    @After
    public void after() {
    	try {
    		LoginContext lc = new LoginContext("qualipso", new UsernamePasswordHandler("root", AllTests.ROOT_ACCOUNT_PASS));
            lc.login();
            
            core.deleteFolder("/testrepos");
        	
        	lc.logout();
    	} catch (Exception e) {
			logger.error(e);
			fail(e.getMessage());
		}
    }
	
    @Test
    public void testCRUDRepository() {
        try {
        	LoginContext lc = new LoginContext("qualipso", new UsernamePasswordHandler("root", AllTests.ROOT_ACCOUNT_PASS));
            lc.login();
        	
            try { 
            	git.createGITRepository("/testrepos/repo1", "MyFirstRepo", "Whaou");
            } catch ( Exception e ) {
            	logger.debug(e);
            	e.printStackTrace();
            	fail(e.getMessage());
            }
            
            try {
            	GITRepository repo = git.readGITRepository("/testrepos/repo1");
            	assertNotNull(repo);
            	assertEquals("MyFirstRepo", repo.getName());
            	assertEquals("Whaou", repo.getDescription());
            } catch ( Exception e ) {
            	fail(e.getMessage());
            }
            
            try {
            	git.updateGITRepository("/testrepos/repo1", "MyFirstRepo!!", "Whaou!");
            	GITRepository repo = git.readGITRepository("/testrepos/repo1");
            	assertNotNull(repo);
            	assertEquals("MyFirstRepo!!", repo.getName());
            	assertEquals("Whaou!", repo.getDescription());
            } catch ( Exception e ) {
            	fail(e.getMessage());
            }
            
            try {
            	git.deleteGITRepository("/testrepos/repo1");
            } catch ( Exception e ) {
            	fail(e.getMessage());
            }
            try {
            	git.readGITRepository("/testrepos/repo1");
            	fail("This repository should not be accessible");
            } catch ( Exception e ) {
            	//
            }
            
            lc.logout();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testPushPullRepository() {
    	try {
    		LoginContext lc = new LoginContext("qualipso", new UsernamePasswordHandler("root", AllTests.ROOT_ACCOUNT_PASS));
            lc.login();
            
            try { 
            	git.createGITRepository("/testrepos/repo1", "MyFirstRepo", "Whaou");
            } catch ( Exception e ) {
            	fail(e.getMessage());
            }
            
         // Create a local git project :
			File workFolder1 = new File(workingDir, "testgit");
			File repositoryFolder1 = new File(workFolder1, ".git");
			Repository repository1 = new Repository(repositoryFolder1);
			repository1.create();

			// Create some file and commit locally
			File file11 = new File(workFolder1, "testfile1.txt");
			FileOutputStream fos11 = new FileOutputStream(file11);
			fos11.write("Yeapa".getBytes());
			fos11.flush();
			fos11.close();

			File file12 = new File(workFolder1, "testfile2.txt");
			FileOutputStream fos12 = new FileOutputStream(file12);
			fos12.write("Youpi !!!".getBytes());
			fos12.flush();
			fos12.close();

			GitIndex index = new GitIndex(repository1);
			logger.debug(workFolder1.getAbsolutePath());
			logger.debug(file11.getAbsolutePath());

			index.add(repository1.getWorkDir(), file11);
			index.add(repository1.getWorkDir(), file12);
			index.write();
			ObjectId treeId = index.writeTree();
			
			ObjectWriter writer = new ObjectWriter(repository1);
			
			Commit commit = new Commit(repository1, new ObjectId[0] );
			commit.setTree(repository1.mapTree(treeId));
			commit.setMessage("dtc");
			commit.setAuthor(new PersonIdent("toto", "toto@titi.com"));
			commit.setCommitter(new PersonIdent("toto", "toto@titi.com"));
			commit.setCommitId(writer.writeCommit(commit));

			final RefUpdate ru = repository1.updateRef(Constants.HEAD);
			ru.setNewObjectId(commit.getCommitId());
			ru.setRefLogMessage("commit: dtc", false);
			if (ru.forceUpdate() == RefUpdate.Result.LOCK_FAILURE) {
				logger.error("Failed to update " + ru.getName() + " to commit " + commit.getCommitId() + ".");
			}

			// Push the commit
			Transport t = Transport.open(repository1, "ssh://root:root@localhost:3333/testrepos/repo1");
			PushConnection pc = t.openPush();
			pc.push(new TextProgressMonitor(), null);
			pc.close();
			t.close();

			// Create another local repository as another user
			File workFolder2 = new File(workingDir, "testgit2");
			File repositoryFolder2 = new File(workFolder2, ".git");
			Repository repository2 = new Repository(repositoryFolder2);
			repository2.create();
			
			Transport t2 = Transport.open(repository2, "ssh://root:root@localhost:3333/testrepos/repo1");
			FetchConnection plc = t2.openFetch();
			plc.fetch(new TextProgressMonitor(), null, null);
			plc.close();
			t2.close();

			//check that files are the same...
            
			
			//Clean everything : 
			
			deleteFolderRecursively(repositoryFolder1);
			deleteFolderRecursively(repositoryFolder2);
			
			git.deleteGITRepository("/testrepos/repo1");
			
			lc.logout();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } 
    }
    
    private void deleteFolderRecursively(File folder) {
		for (File file : folder.listFiles()) {
			if ( file.isDirectory() ) {
				deleteFolderRecursively(file);
			} else {
				file.delete();
			}
		}
		folder.delete();
	}

}
