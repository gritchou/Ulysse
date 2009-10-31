package org.qualipso.factory.test;

import org.junit.Test;
import org.qualipso.factory.security.pap.PAPServiceHelper;

public class PAPServiceHelperTest {
	
	@Test
    public void testBuildPolicy() {
		System.out.println(PAPServiceHelper.buildPolicy("1", "/profiles/toto", "/projects/project1", new String[] {"read","update"}));
	}
	
	@Test
    public void testBuildOwnerPolicy() {
		System.out.println(PAPServiceHelper.buildOwnerPolicy("1", "/profiles/toto", "/projects/project1"));		
	}

}
