package org.qualipso.factory.svn.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Gerald Oster (oster@loria.fr)
 * @date 9 October 2009
 */
@RunWith(Suite.class)
@SuiteClasses(value =  {
    SVNServiceTest.class,
    SVNServeCommandTest.class
    }
)
public class AllTests {

}
