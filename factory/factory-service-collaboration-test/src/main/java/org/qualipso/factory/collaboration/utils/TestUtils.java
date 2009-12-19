package org.qualipso.factory.collaboration.utils;

/**
 * The Class TestUtils.
 */
public class TestUtils {

    /**
     * Normalize for path.
     * 
     * @param name the name
     * 
     * @return the string
     */
    public static String normalizeForPath(String name) {
	String formatedText = name;
	try {
	    if (name != null && name.length() > 0) {
		formatedText = name.toLowerCase().trim().replaceAll(" ", "");
		formatedText = formatedText.replaceAll("[^a-zA-Z0-9]", "");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return formatedText;
    }
}
