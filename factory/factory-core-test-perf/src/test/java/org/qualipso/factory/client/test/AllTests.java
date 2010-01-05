package org.qualipso.factory.client.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.qualipso.factory.client.test.performance.EventQueueServicePTest;
import org.qualipso.factory.client.test.performance.NotificationServicePTest;


/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 28 december 2009
 */
@RunWith(Suite.class)
@SuiteClasses(value =  {
		EventQueueServicePTest.class, NotificationServicePTest.class }
)
public class AllTests {
    public static final String ROOT_ACCOUNT_PASS = "root";
}
