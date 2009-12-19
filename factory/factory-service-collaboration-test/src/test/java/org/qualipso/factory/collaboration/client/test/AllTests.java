package org.qualipso.factory.collaboration.client.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.qualipso.factory.collaboration.client.test.ws.AttachmentsTest;
import org.qualipso.factory.collaboration.client.test.ws.CollaborationFullTest;

@RunWith(Suite.class)
//@SuiteClasses(value = { CollaborationFullTest.class,AttachmentsTest.class })
@SuiteClasses(value = { CollaborationFullTest.class })
public class AllTests {

}
