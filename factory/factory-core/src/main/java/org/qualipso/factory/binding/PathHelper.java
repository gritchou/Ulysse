package org.qualipso.factory.binding;


/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 18 June 2009
 */
public class PathHelper {
	private static final String VALID_PATH_REGEXP = "/|/[a-zA-Z0-9\\-_.~=:&+$,]+(/[a-zA-Z0-9\\-_.~=:&+$,]+)*";

	public static void valid(String path) throws InvalidPathException {
        if (!path.matches(VALID_PATH_REGEXP)) {
            throw new InvalidPathException(path);
        }
    }

    public static String normalize(String path) {
        String[] paths = path.split("/");
        String[] newPaths = new String[paths.length];
        String newPath = "";
        int index = 0;

        for (int i=0; i<paths.length; i++) {
        	if (!paths[i].equals("") && !paths[i].equals(".")) {
        		if (paths[i].equals("..")) {
        			if ( index > 0 ) {
        				index--;
        			}
        		} else {
        			newPaths[index] = paths[i];
        			index++;
        		}
        	}
        }
        
        for (int i=0; i<index; i++) {
            newPath += ("/" + newPaths[i]);
        }

        //root case
        if (newPath == "") {
            newPath = "/";
        }

        return newPath;
    }

    public static boolean isRoot(String path) throws InvalidPathException {
        if (path.equals("/")) {
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean isChild(String parentPath, String childPath) throws InvalidPathException {
    	valid(parentPath);
    	valid(childPath);
    	
        if (!childPath.equals(parentPath) && childPath.startsWith(parentPath + "/")) {
            return true;
        } else {
            return false;
        }
    }

    public static int getDepth(String path) throws InvalidPathException {
        valid(path);
        String npath = path.substring(1);

        String[] nodes = npath.split("/");

        return nodes.length;
    }

    public static String getParentPath(String path) throws InvalidPathException {
        valid(path);
        if ( path.length() == 1 ) {
        	throw new InvalidPathException("root path has no parent");
        }
        String npath = path.substring(1);

        if (npath.lastIndexOf("/") == -1) {
            return "/";
        }

        return "/" + npath.substring(0, npath.lastIndexOf("/"));
    }
    
    public static String getPathPart(String path) throws InvalidPathException {
        valid(path);
        String npath = path.substring(1);

        if (npath.lastIndexOf("/") <= 0) {
            return npath;
        }

        return npath.substring(npath.lastIndexOf("/") + 1);
    }

    public static String[] splitPath(String path) throws InvalidPathException {
        valid(path);
        String npath = path.substring(1);

        return npath.split("/");
    }

}
