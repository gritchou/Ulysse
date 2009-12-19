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
package org.qualipso.factory.test.jmock.action;

import org.hamcrest.Description;

import org.jmock.api.Action;
import org.jmock.api.Invocation;


public class ReturnValueAndSaveParamsAction implements Action {
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
