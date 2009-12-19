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
package org.qualipso.factory.greeting.test.jmock.action;

import org.hamcrest.Description;

import org.jmock.api.Action;
import org.jmock.api.Invocation;

import java.util.Vector;


public class SaveParamsAction implements Action {
    private Vector<Object> params;

    public SaveParamsAction(Vector<Object> params) {
        this.params = params;
    }

    public Object invoke(Invocation invocation) throws Throwable {
        for (Object param : invocation.getParametersAsArray()) {
            params.add(param);
        }

        return null;
    }

    public void describeTo(Description description) {
        description.appendText("save params in ");
        description.appendValue(params);
    }

    public static <T> Action saveParams(Vector<Object> params) {
        return new SaveParamsAction(params);
    }
}
