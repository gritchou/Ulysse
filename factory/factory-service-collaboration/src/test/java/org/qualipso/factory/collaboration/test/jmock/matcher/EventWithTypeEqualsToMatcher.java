package org.qualipso.factory.collaboration.test.jmock.matcher;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.qualipso.factory.notification.Event;

// TODO: Auto-generated Javadoc
/**
 * The Class EventWithTypeEqualsToMatcher.
 */
public class EventWithTypeEqualsToMatcher extends TypeSafeMatcher<Event> {
    
    /** The type. */
    private String type;

    /**
     * Instantiates a new event with type equals to matcher.
     * 
     * @param type the type
     */
    public EventWithTypeEqualsToMatcher(String type) {
        this.type = type;
    }

    /* (non-Javadoc)
     * @see org.hamcrest.TypeSafeMatcher#matchesSafely(java.lang.Object)
     */
    public boolean matchesSafely(Event e) {
        return e.getEventType().equals(type);
    }

    /* (non-Javadoc)
     * @see org.hamcrest.SelfDescribing#describeTo(org.hamcrest.Description)
     */
    public void describeTo(Description description) {
        description.appendText("an event with type equals to ").appendValue(type);
    }
    
    /**
     * An event with type equals to.
     * 
     * @param type the type
     * 
     * @return the matcher< event>
     */
    @Factory
    public static Matcher<Event> anEventWithTypeEqualsTo( String type ) {
        return new EventWithTypeEqualsToMatcher(type);
    }
}
