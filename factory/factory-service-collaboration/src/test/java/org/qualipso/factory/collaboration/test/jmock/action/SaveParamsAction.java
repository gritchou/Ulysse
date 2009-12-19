package org.qualipso.factory.collaboration.test.jmock.action;

import java.util.Vector;

import org.hamcrest.Description;
import org.jmock.api.Action;
import org.jmock.api.Invocation;

// TODO: Auto-generated Javadoc
/**
 * The Class SaveParamsAction.
 */
public class SaveParamsAction implements Action {
    
    /** The params. */
    private Vector<Object> params;

    /**
     * Instantiates a new save params action.
     * 
     * @param params the params
     */
    public SaveParamsAction(Vector<Object> params) {
        this.params = params;
    }

    /* (non-Javadoc)
     * @see org.jmock.api.Invokable#invoke(org.jmock.api.Invocation)
     */
    public Object invoke(Invocation invocation) throws Throwable {
    	for ( Object param : invocation.getParametersAsArray() ) {
    		params.add(param);
    	}
    	return null;
    }

    /* (non-Javadoc)
     * @see org.hamcrest.SelfDescribing#describeTo(org.hamcrest.Description)
     */
    public void describeTo(Description description) {
        description.appendText("save params in ");
        description.appendValue(params);
    }
    
    /**
     * Save params.
     * 
     * @param params the params
     * 
     * @return the action
     */
    public static <T> Action saveParams(Vector<Object> params) {
        return new SaveParamsAction(params);
    }
}
