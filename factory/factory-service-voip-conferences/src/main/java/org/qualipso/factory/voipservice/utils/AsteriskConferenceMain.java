package org.qualipso.factory.voipservice.utils;

import org.apache.log4j.Logger;
import org.qualipso.factory.voipservice.VoIPConferenceServiceBean;
import org.qualipso.factory.voipservice.VoIPConferenceServiceException;

/**
 * @author <a href="mailto:janny@man.poznan.pl">Dariusz Janny</a>
 * @company Poznan Supercomputing and Networking Center
 * @license LGPL
 * @project QualiPSo 
 * @date 24/07/2009
 */
public class AsteriskConferenceMain {
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(AsteriskConferenceMain.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String userId = "test";
		String pass = "";

		VoIPConferenceServiceBean ads = null;
		try {
			ads = new VoIPConferenceServiceBean();
			ads.resetDatabase(userId, pass);
			AsteriskConferenceUtils.close();
		} catch (VoIPConferenceServiceException e) {
			e.printStackTrace();
		}
	}
}


