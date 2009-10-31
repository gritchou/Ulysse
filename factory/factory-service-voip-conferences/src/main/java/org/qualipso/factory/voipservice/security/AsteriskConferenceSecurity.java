package org.qualipso.factory.voipservice.security;



/**
 * @author <a href="mailto:janny@man.poznan.pl">Dariusz Janny</a>
 * @company Poznan Supercomputing and Networking Center
 * @license LGPL
 * @project QualiPSo 
 * @date 24/07/2009
 */
public abstract interface AsteriskConferenceSecurity {
	public abstract boolean checkPermission(AsteriskConferenceSecurityParams params);
}
