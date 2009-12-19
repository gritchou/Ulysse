package org.qualipso.factory.collaboration.test.jmock.action;

import org.hamcrest.Description;
import org.jmock.api.Action;
import org.jmock.api.Invocation;

// TODO: Auto-generated Javadoc
/**
 * The Class ReturnValueAndSaveParamsAction.
 */
public class ReturnValueAndSaveParamsAction  implements Action {
    
    /** The result. */
    private Object result;
    
    /** The params. */
    private Object[] params;

    /**
     * Instantiates a new return value and save params action.
     * 
     * @param result the result
     * @param params the params
     */
    public ReturnValueAndSaveParamsAction(Object result, Object[] params) {
        this.result = result;
    }

    /* (non-Javadoc)
     * @see org.jmock.api.Invokable#invoke(org.jmock.api.Invocation)
     */
    public Object invoke(Invocation invocation) throws Throwable {
    	params = invocation.getParametersAsArray();
        return result;
    }

    /* (non-Javadoc)
     * @see org.hamcrest.SelfDescribing#describeTo(org.hamcrest.Description)
     */
    public void describeTo(Description description) {
        description.appendText("returns ");
        description.appendValue(result);
        description.appendText("save params in ");
        description.appendValue(params);
    }
    
    /**
     * Return value and save params.
     * 
     * @param result the result
     * @param params the params
     * 
     * @return the action
     */
    public static <T> Action returnValueAndSaveParams(Object result, Object[] params) {
        return new ReturnValueAndSaveParamsAction(result, params);
    }
}
