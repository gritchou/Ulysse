package org.qualipso.factory.voipservice.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.qualipso.factory.voipservice.test.entity.ConferenceDetailsTest;
import org.qualipso.factory.voipservice.test.entity.ParticipantsInfoTest;
import org.qualipso.factory.voipservice.test.sessionbean.VoIPConferenceServiceTest;

/**
 * @author <a href="mailto:janny@man.poznan.pl">Dariusz Janny</a>
 * @company Poznan Supercomputing and Networking Center
 * @license LGPL
 * @project QualiPSo 
 * @date 24/07/2009
 */

@RunWith(Suite.class)
@SuiteClasses(value = { 
		ConferenceDetailsTest.class,
		ParticipantsInfoTest.class,
		VoIPConferenceServiceTest.class})
public class AllTests {
}
