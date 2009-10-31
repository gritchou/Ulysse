package org.qualipso.factory.collaboration.calendar;

import javax.xml.ws.WebFault;

import org.qualipso.factory.FactoryException;

@WebFault
@SuppressWarnings("serial")
public class CalendarServiceException extends FactoryException
{

    public CalendarServiceException(String message, Exception rootCause)
    {
	super(message, rootCause);
    }

    public CalendarServiceException(String message)
    {
	super(message);
    }

    public CalendarServiceException(Exception rootCause)
    {
	super(rootCause);
    }
}
