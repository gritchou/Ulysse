/*
 *
 * Qualipso Factory
 * Copyright (C) 2006-2010 INRIA
 * http://www.inria.fr - molli@loria.fr
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of LGPL. See licenses details in LGPL.txt
 *
 * Initial authors :
 *
 * Jérôme Blanchard / INRIA
 * Pascal Molli / Nancy Université
 * Gérald Oster / Nancy Université
 * Christophe Bouthier / INRIA
 * 
 */
package org.qualipso.factory.binding;


/**
 * Helper class to manage common usefull methods on paths.
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 18 June 2009
 */
public class PathHelper {
    /**
     * Path validation regexp.
     */
    private static final String VALID_PATH_REGEXP = "/|/[a-zA-Z0-9\\-_.~=:&+$,]+(/[a-zA-Z0-9\\-_.~=:&+$,]+)*";

    /**
     * Thrown an exception if gievn path is not valid.
     *
     * @param path the path to valid
     * @throws InvalidPathException if the path does not match regexp
     */
    public static void valid(String path) throws InvalidPathException {
        if (!path.matches(VALID_PATH_REGEXP)) {
            throw new InvalidPathException(path);
        }
    }

    /**
     * Normalize a path removing double separators, interpreting relative references (. or ..)
     *
     * @param path the path to normalize
     * @return a normalized version of this path
     * @throws InvalidPathException if the path is not valid
     */
    public static String normalize(String path) throws InvalidPathException {
        String[] paths = path.split("/");
        String[] newPaths = new String[paths.length];
        String newPath = "";
        int index = 0;

        for (int i = 0; i < paths.length; i++) {
            if (!paths[i].equals("") && !paths[i].equals(".")) {
                if (paths[i].equals("..")) {
                    if (index > 0) {
                        index--;
                    }
                } else {
                    newPaths[index] = paths[i];
                    index++;
                }
            }
        }

        for (int i = 0; i < index; i++) {
            newPath += ("/" + newPaths[i]);
        }

        //root case
        if (newPath == "") {
            newPath = "/";
        }

        valid(newPath);

        return newPath;
    }

    /**
     * Check if this path is the root path (ie /)
     *
     * @param path the path to test
     * @return true  if the path is the root node, false instead
     * @throws InvalidPathException if the path is not valid
     */
    public static boolean isRoot(String path) throws InvalidPathException {
        String npath = normalize(path);

        if (npath.equals("/") || npath.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check if a path is a child of another one.
     *
     * @param parentPath
     * @param childPath
     * @return true if the path is a child
     * @throws InvalidPathException if one of the path is not valid
     */
    public static boolean isChild(String parentPath, String childPath)
        throws InvalidPathException {
        String nParentPath = normalize(parentPath);
        String nChildPath = normalize(childPath);

        if (!nChildPath.equals(nParentPath) && nChildPath.startsWith(nParentPath + "/")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Calculate the depth of a path.
     *
     * @param path
     * @return the integer value of the depth of this path
     * @throws InvalidPathException if the path is not valid
     */
    public static int getDepth(String path) throws InvalidPathException {
        String npath = normalize(path);
        npath = npath.substring(1);

        String[] nodes = npath.split("/");

        return nodes.length;
    }

    /**
     * Return the parent path of a given path.
     *
     * @param path
     * @return the parent path
     * @throws InvalidPathException if the path is not valid or if the path is the root path
     */
    public static String getParentPath(String path) throws InvalidPathException {
        String npath = normalize(path);

        if (isRoot(npath)) {
            throw new InvalidPathException("root path has no parent");
        }

        npath = npath.substring(1);

        if (npath.lastIndexOf("/") == -1) {
            return "/";
        }

        return "/" + npath.substring(0, npath.lastIndexOf("/"));
    }

    /**
     * Return the leaf of a path
     *
     * @param path
     * @return the leaf part of this path
     * @throws InvalidPathException if the path is not valid
     */
    public static String getPathPart(String path) throws InvalidPathException {
        String npath = normalize(path);
        npath = npath.substring(1);

        if (npath.lastIndexOf("/") <= 0) {
            return npath;
        }

        return npath.substring(npath.lastIndexOf("/") + 1);
    }

    /**
     * Split a path into segment
     *
     * @param path the path to split
     * @return a String array containing each path segment
     * @throws InvalidPathException if the path is not valid
     */
    public static String[] splitPath(String path) throws InvalidPathException {
        String npath = normalize(path);
        npath = npath.substring(1);

        return npath.split("/");
    }
}
