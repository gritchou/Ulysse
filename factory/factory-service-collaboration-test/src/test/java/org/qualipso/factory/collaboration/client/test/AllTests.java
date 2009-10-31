package org.qualipso.factory.collaboration.client.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.qualipso.factory.collaboration.client.test.ws.CollaborationFullTest;
import org.qualipso.factory.collaboration.client.test.ws.CalendarServiceWSTest;
import org.qualipso.factory.collaboration.client.test.ws.DocumentServiceWSTest;
import org.qualipso.factory.collaboration.client.test.ws.ForumServiceWSTest;

@RunWith(Suite.class)

@SuiteClasses(value =  {
	CollaborationFullTest.class}
)
public class AllTests {
	
}
