package org.qualipso.factory.collaboration.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.qualipso.factory.collaboration.test.entity.CalendarItemTest;
import org.qualipso.factory.collaboration.test.entity.CollaborationFolderTest;
import org.qualipso.factory.collaboration.test.entity.DocumentTest;
import org.qualipso.factory.collaboration.test.entity.ForumTest;
import org.qualipso.factory.collaboration.test.entity.ThreadMessageTest;
import org.qualipso.factory.collaboration.test.sessionbean.CalendarServiceTest;
import org.qualipso.factory.collaboration.test.sessionbean.DocumentServiceTest;
import org.qualipso.factory.collaboration.test.sessionbean.ForumServiceTest;

/**
 * The Class AllTests.
 */
@RunWith(Suite.class)
@SuiteClasses(value = { 
	DocumentTest.class,
	CollaborationFolderTest.class,
	CalendarItemTest.class,
	ForumTest.class,
	ThreadMessageTest.class,
	CalendarServiceTest.class,
	DocumentServiceTest.class,
	ForumServiceTest.class})		
public class AllTests {
	
	
}
