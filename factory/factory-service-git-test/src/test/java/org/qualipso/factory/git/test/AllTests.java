package org.qualipso.factory.git.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.qualipso.factory.git.test.ws.AllWSTests;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 24 September 2009
 */
@RunWith(Suite.class)
@SuiteClasses(value =  {
    AllWSTests.class}
)
public class AllTests {
	
	public static final String ROOT_ACCOUNT_PASS = "root";
	

}
