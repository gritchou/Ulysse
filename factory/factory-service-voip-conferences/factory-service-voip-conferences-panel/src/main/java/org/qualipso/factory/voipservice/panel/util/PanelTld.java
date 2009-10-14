package org.qualipso.factory.voipservice.panel.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PanelTld {
	static private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	/**
	 * @return
	 */
	public static DateFormat getDateFormat() {
		return dateFormat;
	}

	/**
	 * @param dateLong
	 * @return
	 */
	public static String formatDate(Long dateLong) {
		if (dateLong == null) {
			return "";
		} else if (dateLong<0) {
			return "Not set";
		}
		Date date = new Date(dateLong * 1000);
		return dateFormat.format(date);
	}

}
