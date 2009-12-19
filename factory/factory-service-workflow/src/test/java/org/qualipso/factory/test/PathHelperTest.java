package org.qualipso.factory.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.binding.PathHelper;

public class PathHelperTest {
	
	private String[] validPaths = new String[] {
			"/toto", 
			"/toto/tutu", 
			"/~home", 
			"/toto-tutu",
			"/titi.tutu",
			"/toto_tata",
			"/",
			"/_____test",
			"/toto-tutu/titi.tutu/toto_tata/____test",
			"/test1234/8917",
			"/."};
	
	private String[] invalidPaths = new String[] {
			"toto", 
			"", 
			"~home", 
			"toto-tutu",
			"titi.tutu",
			"toto_tata",
			"toto/tutu",
			"_____test",
			"toto-tutu/titi.tutu/toto_tata/____test",
			"/toto|2",
			"/(aka12)",
			"//test",
			"///test",
			"/tésté", 
			"/éèëêôöûüùîïÿ",
			"/./",
			"//",
			"////"};
	
	private String [] normalizePaths = new String[] {
			"",
			"toto",
			"toto/tutu",
			"//test",
			"//",
			"///test///.//ttest"};
	
	@Test
    public void testValidPath() throws Exception {
    	for (String path : validPaths) {
    		try {
    			PathHelper.valid(path);
    		} catch ( InvalidPathException ive ) {
    			fail("path [" + path + "] should be valid");
    		}
    	}
    }
    
    @Test
    public void testValidPath2() throws Exception {
    	for (String path : invalidPaths) {
    		try {
    			PathHelper.valid(path);
    			fail("path [" + path + "] should NOT be valid");
    		} catch ( InvalidPathException ive ) {
    			//
    		}
    	}
    }

    @Test
    public void testNormalizePath() {
        for ( String path : normalizePaths ) {
        	try {
    			PathHelper.valid(path);
    			fail("path [" + path + "] should NOT be valid BEFORE normalization");
    		} catch ( InvalidPathException ive ) {
    			//
    		}
    		try {
    			PathHelper.valid(PathHelper.normalize(path));
    		} catch ( InvalidPathException ive ) {
    			fail("path [" + path + "] should be valid AFTER normalization");
    		}
        }
        try {
			assertEquals("/test", PathHelper.normalize("/././././test/./"));
	        assertEquals("/", PathHelper.normalize("/test/../../../bidi/../../.."));
	        assertEquals("/bidi", PathHelper.normalize("/test/../../../bidi"));
	        assertEquals("/bidi/budu", PathHelper.normalize("/test/../bidi/bada/../budu"));
	        assertEquals("/bidi/budu", PathHelper.normalize("/test/../bidi/bada/byby/.././../budu"));
		} catch (InvalidPathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    
    @Test
    public void testPathDepth() {
        try {
            assertEquals(1, PathHelper.getDepth("/toto"));
            assertEquals(7, PathHelper.getDepth("/1/2/3/4/5/6/7"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetParentPath() throws Exception {
        assertEquals("/", PathHelper.getParentPath("/toto"));
        assertEquals("/toto", PathHelper.getParentPath("/toto/titi"));
        assertEquals("/toto", PathHelper.getParentPath("/toto/titi.tata"));
        assertEquals("/toto.titi", PathHelper.getParentPath("/toto.titi/tata"));
    }
    
    @Test
    public void testGetPathPart() throws Exception {
    	assertEquals("", PathHelper.getPathPart("/"));
        assertEquals("toto", PathHelper.getPathPart("/toto"));
        assertEquals("titi", PathHelper.getPathPart("/toto/titi"));
        assertEquals("titi.tata", PathHelper.getPathPart("/toto/titi.tata"));
        assertEquals("tata", PathHelper.getPathPart("/toto.titi/tata"));
    }

    @Test
    public void splitPathTest() {
        try {
            String[] split1 = { "toto", "titi" };
            String[] split2 = { "toto", "titi", "tata.4", "tete~9", "tutu_8", "tyty-ty", "tagada" };
            assertArrayEquals(split1, PathHelper.splitPath("/toto/titi"));
            assertArrayEquals(split2, PathHelper.splitPath("/toto/titi/tata.4/tete~9/tutu_8/tyty-ty/tagada"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
