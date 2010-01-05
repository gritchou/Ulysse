package org.qualipso.factory.notification;

import java.text.SimpleDateFormat;

public class EventLoggerFormater {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSSS ZZ");
	private static String fieldSeparator = ",";
	private static String lineSeparator = "\r\n";
	
	public static String formatEvent(Event e) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[" + sdf.format(e.getDate()) + "]");
		buffer.append(fieldSeparator);
		buffer.append(e.getFromResource());
		buffer.append(fieldSeparator);
		buffer.append(e.getResourceType());
		buffer.append(fieldSeparator);
		buffer.append(e.getEventType());
		buffer.append(fieldSeparator);
		buffer.append(e.getThrowedBy());
		buffer.append(fieldSeparator);
		buffer.append(e.getArgs());
		buffer.append(fieldSeparator);
		buffer.append(lineSeparator);
		return buffer.toString();
	}

}
