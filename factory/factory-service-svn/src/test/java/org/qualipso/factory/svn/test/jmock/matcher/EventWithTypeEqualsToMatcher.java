package org.qualipso.factory.svn.test.jmock.matcher;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.qualipso.factory.notification.Event;

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
    public static Matcher<Event> anEventWithTypeEqualsTo( String type ) {
        return new EventWithTypeEqualsToMatcher(type);
    }
}
