package org.qualipso.factory.git.test;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.qualipso.factory.git.test.sb.GITServiceSBTest;
import org.spearce.jgit.dircache.DirCache;
import org.spearce.jgit.dircache.DirCacheEntry;
import org.spearce.jgit.lib.Commit;
import org.spearce.jgit.lib.Constants;
import org.spearce.jgit.lib.CoreConfig;
import org.spearce.jgit.lib.GitIndex;
import org.spearce.jgit.lib.ObjectId;
import org.spearce.jgit.lib.ObjectWriter;
import org.spearce.jgit.lib.PersonIdent;
import org.spearce.jgit.lib.Ref;
import org.spearce.jgit.lib.RefComparator;
import org.spearce.jgit.lib.RefUpdate;
import org.spearce.jgit.lib.Repository;
import org.spearce.jgit.lib.RepositoryConfig;
import org.spearce.jgit.lib.TransferConfig;
import org.spearce.jgit.lib.UserConfig;
import org.spearce.jgit.lib.GitIndex.Entry;
import org.spearce.jgit.revwalk.RevCommit;
import org.spearce.jgit.revwalk.RevTree;
import org.spearce.jgit.revwalk.RevWalk;
import org.spearce.jgit.treewalk.TreeWalk;

public class LocalGitTest {

	private static Log logger = LogFactory.getLog(GITServiceSBTest.class);
	private File workingDir;

	@Before
	public void before() {
		workingDir = new File(new File("target/repos").getAbsolutePath());
	}

	@After
	public void after() {
		// deleteFolderRecursively(workingDir);
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

	@Test
	public void testCreateAddAndCommitToLocalRepository() {
		try {
			// Create a local git project :
			File workFolder1 = new File(workingDir, "testgit");
			File repositoryFolder1 = new File(workFolder1, ".git");
			Repository repository1 = new Repository(repositoryFolder1);
			repository1.create();

			dumpRepository(repository1);

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

			// Start tracking resources and commit the new tree :
			// DirCache dc = DirCache.lock(repository1);
			// assertTrue(dc.getEntryCount() == 0);
			// DirCacheBuilder dcb = dc.builder();
			// DirCacheEntry dce1 = new DirCacheEntry("testfile1.txt");
			// dce1.setFileMode(FileMode.REGULAR_FILE);
			// dce1.setAssumeValid(true);
			// dce1.setLastModified(file11.lastModified());
			// //dce1.setLength(file11.length());
			// ObjectWriter ow1 = new ObjectWriter(repository1);
			// ObjectId objectId1 = ow1.writeBlob(file11);
			// dce1.setObjectId(objectId1);
			//			
			// DirCacheEntry dce2 = new DirCacheEntry("testfile2.txt");
			// dce2.setFileMode(FileMode.REGULAR_FILE);
			// dce2.setAssumeValid(true);
			// dce2.setLastModified(file12.lastModified());
			// //dce2.setLength(file12.length());
			// ObjectWriter ow2 = new ObjectWriter(repository1);
			// ObjectId objectId2 = ow2.writeBlob(file12);
			// dce2.setObjectId(objectId2);
			//			
			// dcb.add(dce1);
			// dcb.add(dce2);
			// assertTrue(dcb.commit());
			// assertTrue(dc.lock());
			// dc.write();
			// assertTrue(dc.commit());
			// assertTrue(dc.getEntryCount() == 2);

			GitIndex index = new GitIndex(repository1);
			logger.debug(workFolder1.getAbsolutePath());
			logger.debug(file11.getAbsolutePath());

			index.add(repository1.getWorkDir(), file11);
			index.add(repository1.getWorkDir(), file12);
			index.write();
			ObjectId treeId = index.writeTree();

			ObjectWriter writer = new ObjectWriter(repository1);

			Commit commit = new Commit(repository1, new ObjectId[0]);
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

			dumpRepository(repository1);

			// // Push the commit
			// Transport t = Transport.open(repository1, "ssh://root:root@localhost:3333/testrepos/repo1");
			// PushConnection pc = t.openPush();
			// pc.push(null, null);
			// pc.close();
			// t.close();
			//
			// // Create another local repository as another user
			// File workFolder2 = new File(workingDir, "testgit2");
			// File repositoryFolder2 = new File(workFolder2, ".git");
			// Repository repository2 = new Repository(repositoryFolder2);
			// repository2.create();
			//			
			// Transport t2 = Transport.open(repository2, "ssh://root:root@localhost:3333/testrepos/repo1");
			// FetchConnection plc = t2.openFetch();
			// plc.fetch(null, null, null);
			// plc.close();
			// t2.close();
			//
			// dumpRepository(repository2);

			repository1.close();

		} catch (Exception e) {
			logger.error(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testBrowseWorkingDir() {
		logger.debug("");
		logger.debug("##### working dir #####");
		try {
			File workFolder = new File(new File(ClassLoader.getSystemResource("log4j.xml").getPath()).getParentFile(), "git");
			File repositoryFolder = new File(workFolder, ".git");
			Repository repository = new Repository(repositoryFolder);

			ObjectId retrieveStart = repository.resolve(Constants.MASTER);
			RevWalk revWalk = new RevWalk(repository);
			RevCommit entry = revWalk.parseCommit(retrieveStart);
			RevTree revTree = entry.getTree();

			TreeWalk walk = new TreeWalk(repository);
			walk.addTree(revTree);
			
			while (walk.next()) {
				if (walk.isSubtree()) {
					logger.debug("tree:   " + walk.getNameString());
				} else {
					logger.debug("blob:   " + walk.getNameString());
				}
			}

			repository.close();
		} catch (Exception e) {
			logger.error(e);
			fail(e.getMessage());
		}
		logger.debug("############################");
		logger.debug("");
	}

	@Test
	public void testBrowseCommits() {
		logger.debug("");
		logger.debug("##### commit history #####");
		try {
			File workFolder = new File(new File(ClassLoader.getSystemResource("log4j.xml").getPath()).getParentFile(), "git");
			File repositoryFolder = new File(workFolder, ".git");
			Repository repository = new Repository(repositoryFolder);

			// DateFormat df = new SimpleDateFormat("EEEE dd MMM HH:mm:ss yyyy ZZZZZ", Locale.FRANCE);
			DateFormat df = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.FULL, SimpleDateFormat.LONG, Locale.FRANCE);

			ObjectId head = repository.resolve(Constants.MASTER);

			RevWalk walk = new RevWalk(repository);
			walk.markStart(walk.parseCommit(head));

			for (RevCommit commit : walk) {
				RevTree tree = commit.getTree();
				logger.debug("commit  : " + commit.getName());
				logger.debug("tree    : " + tree.getName());
				if (commit.getParentCount() > 0) {
					logger.debug("parent  : " + commit.getParent(0).getName());
				}
				logger.debug("author  : " + commit.getAuthorIdent().getName() + " <" + commit.getAuthorIdent().getEmailAddress() + ">");
				df.setTimeZone(commit.getAuthorIdent().getTimeZone());
				logger.debug("date    : " + df.format(commit.getAuthorIdent().getWhen()));
				logger.debug("message :");
				logger.debug("");
				String[] lines = commit.getFullMessage().split("\n");
				for (String line : lines) {
					logger.debug("\t" + line);
				}
				logger.debug("");
			}

			repository.close();
		} catch (Exception e) {
			logger.error(e);
			fail(e.getMessage());
		}
		logger.debug("############################");
		logger.debug("");
	}

	@Test
	public void testListBranches() {
		logger.debug("");
		logger.debug("##### listing branches #####");
		try {
			File workFolder = new File(new File(ClassLoader.getSystemResource("log4j.xml").getPath()).getParentFile(), "git");
			File repositoryFolder = new File(workFolder, ".git");
			Repository repository = new Repository(repositoryFolder);

			Map<String, Ref> refs = repository.getAllRefs();
			Map<String, Ref> brefs = new HashMap<String, Ref>();
			Ref head = refs.get(Constants.HEAD);

			logger.debug("branches : ");
			if (head != null) {
				String current = head.getName();
				for (Ref ref : RefComparator.sort(refs.values())) {
					//logger.debug(ref);
					final String name = ref.getName();
					if (name.startsWith(Constants.R_HEADS)) {
						brefs.put(name, ref);
					}
				}
				for (String name : brefs.keySet()) {
					if (name.equals(current)) {
						logger.debug("* " + name.substring(name.indexOf('/', 5) + 1));
					} else {
						logger.debug("  " + name.substring(name.indexOf('/', 5) + 1));
					}
				}
			}

			repository.close();
		} catch (Exception e) {
			logger.error(e);
			fail(e.getMessage());
		}
		logger.debug("############################");
		logger.debug("");
	}

	private void dumpRepository(Repository rep) throws IOException {
		logger.debug("###### Repository DUMP #########");
		logger.debug("repository directory : " + rep.getDirectory());
		logger.debug("repository work dir " + rep.getWorkDir());
		logger.debug("repository branch : " + rep.getBranch());
		logger.debug("repository full branch : " + rep.getFullBranch());
		logger.debug("repository config : ");
		RepositoryConfig config = rep.getConfig();
		logger.debug("-> CONFIG: authorName: " + config.getAuthorName() + ", authorEmail: " + config.getAuthorEmail() + ", commiterName: "
				+ config.getCommitterName() + ", commiterEmail: " + config.getCommitterEmail());
		CoreConfig core = config.getCore();
		logger.debug("-----> CORE CONFIG: compression: " + core.getCompression() + ", packIndexVersion: " + core.getPackIndexVersion());
		TransferConfig transfer = config.getTransfer();
		logger.debug("-----> TRANSFER CONFIG: isFSCKObjects: " + transfer.isFsckObjects());
		UserConfig user = config.getUserConfig();
		logger.debug("-----> USER CONFIG: authorName: " + user.getAuthorName() + ", authorEmail: " + user.getAuthorEmail() + ", commiterName: "
				+ user.getCommitterName() + ", commiterEmail: " + user.getCommitterEmail());
		logger.debug("repository state : " + rep.getRepositoryState());
		logger.debug("repository objects dir : " + rep.getObjectsDirectory());
		logger.debug("repository refs : ");
		Map<String, Ref> refs = rep.getAllRefs();
		for (String key : refs.keySet()) {
			logger.debug("-> REF: key: " + key + ", value: " + refs.get(key));
		}
		logger.debug("repository tags : ");
		Map<String, Ref> tags = rep.getTags();
		for (String key : refs.keySet()) {
			logger.debug("-> TAGS: key: " + key + ", value: " + refs.get(key));
		}
		logger.debug("repositroy index : ");
		GitIndex index = rep.getIndex();
		Entry[] entries = index.getMembers();
		for (Entry entry : entries) {
			logger.debug("-> ENTRY: name: " + entry.getName() + ", size: " + entry.getSize() + ", stage: " + entry.getStage() + ", modebits: "
					+ entry.getModeBits() + ", objectId: " + entry.getObjectId());
		}
		logger.debug("repositroy dir cache : ");
		DirCache dirCache = DirCache.lock(rep);
		for (int i = 0; i < dirCache.getEntryCount(); i++) {
			DirCacheEntry entry = dirCache.getEntry(i);
			logger.debug("-> ENTRY: filemode: " + entry.getFileMode() + ", length: " + entry.getLength() + ", stage: " + entry.getStage() + ", lastmodified: "
					+ entry.getLastModified() + ", objectId: " + entry.getObjectId() + ", pathString: " + entry.getPathString() + ", rawMode: "
					+ entry.getRawMode());
		}
		// logger.debug("repositroy dir cache with iterator: ");
		// DirCacheIterator dcIterator = new DirCacheIterator(dirCache);
		// do ( !dcIterator.eof() ) {
		// DirCacheEntry entry = dcIterator.getDirCacheEntry();
		// logger.debug("-> ENTRY: filemode: " + entry.getFileMode() + ", length: " + entry.getLength() + ", stage: " + entry.getStage() + ", lastmodified: " +
		// entry.getLastModified() + ", objectId: " + entry.getObjectId() + ", pathString: " + entry.getPathString() + ", rawMode: " + entry.getRawMode());
		// dcIterator.next(1);
		// }
		dirCache.unlock();
		logger.debug("###################################");

	}

}
