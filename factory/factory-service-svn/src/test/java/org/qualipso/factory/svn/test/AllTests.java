package org.qualipso.factory.svn.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.qualipso.factory.svn.utils.FilterUtilsTest;
import org.qualipso.factory.svn.utils.SVNPropertiesTest;

/**
 * @author Gerald Oster (oster@loria.fr)
 * @date 9 October 2009
 */
@RunWith(Suite.class)
@SuiteClasses(value =  {
    SVNServiceTest.class,
    SVNServiceLocalTest.class,
    FilterUtilsTest.class,
    SVNPropertiesTest.class
    }
)
public class AllTests {

}
