/**
 * 
 * Copyright (C) 2006-2010 THALES
 * http://www.thalesgroup.fr - gregory.cunha@thalesgroup.com
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of the license LGPL.
 *
 * Initial author :
 * Gregory Cunha from Thales Service, THERESIS Competence Center Open Source Software
 *
 */
package org.qualipso.factory.svn.ssh.command.filter;

/**
 * Define operation for SVN
 *
 */
public enum SVNOperationType {

	/**
	 * Commit in repositry
	 */
	COMMIT {

		/* (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return "commit";
		}
	},
	
	/**
	 * Update a local view of the repository
	 */
	UPDATE {

		/* (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return "update";
		}
	},
	
	/**
	 * Diff between a local view and a repository
	 */
	DIFF {

		/* (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return "diff";
		}
	},
}
