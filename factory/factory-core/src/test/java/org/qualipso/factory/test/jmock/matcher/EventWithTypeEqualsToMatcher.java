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
package org.qualipso.factory.test.jmock.matcher;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.qualipso.factory.eventqueue.entity.Event;

public class EventWithTypeEqualsToMatcher extends TypeSafeMatcher<Event> {
    private String type;

    public EventWithTypeEqualsToMatcher(String type) {
        this.type = type;
    }

    public boolean matchesSafely(Event e) {
        return e.getEventType().equals(type);
    }

    public void describeTo(Description description) {
        description.appendText("an event with type equals to ").appendValue(type);
    }

    @Factory
    public static Matcher<Event> anEventWithTypeEqualsTo(String type) {
        return new EventWithTypeEqualsToMatcher(type);
    }
}
