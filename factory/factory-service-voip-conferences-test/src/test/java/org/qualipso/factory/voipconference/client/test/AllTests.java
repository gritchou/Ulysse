package org.qualipso.factory.voipconference.client.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.qualipso.factory.voipconference.client.test.ws.VoIPConferenceServiceWSTest;

@RunWith(Suite.class)
@SuiteClasses(value =  {
	VoIPConferenceServiceWSTest.class}
)
public class AllTests {
	
}
