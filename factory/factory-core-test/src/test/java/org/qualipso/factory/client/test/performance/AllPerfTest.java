package org.qualipso.factory.client.test.performance;



import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.qualipso.factory.client.test.sb.AllSBTests;
import org.qualipso.factory.client.test.ws.AllWSTests;



/**
 * @author Huriye Yuksel
 * @date 27  novembre 2009
 */

@RunWith(Suite.class)
@SuiteClasses(value =  {
        EventQueueServicePTest.class}
)


public class AllPerfTest {
    
    
}
