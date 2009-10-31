package org.qualipso.factory.project.test.jmock.action;

import org.hamcrest.Description;
import org.jmock.api.Action;
import org.jmock.api.Invocation;

public class ReturnValueAndSaveParamsAction  implements Action {
    private Object result;
    private Object[] params;

    public ReturnValueAndSaveParamsAction(Object result, Object[] params) {
        this.result = result;
    }

    public Object invoke(Invocation invocation) throws Throwable {
    	params = invocation.getParametersAsArray();
        return result;
    }

    public void describeTo(Description description) {
        description.appendText("returns ");
        description.appendValue(result);
        description.appendText("save params in ");
        description.appendValue(params);
    }
    
    public static <T> Action returnValueAndSaveParams(Object result, Object[] params) {
        return new ReturnValueAndSaveParamsAction(result, params);
    }
}