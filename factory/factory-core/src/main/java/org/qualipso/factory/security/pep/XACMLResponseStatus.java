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
package org.qualipso.factory.security.pep;


/**
 * A Enumeration of possible XACML response status : <br/>
 * <ul>
 * <li>PERMIT, the action is permit</li>
 * <li>DENY, the action is deny</li>
 * <li>INDETERMINATE, it was not possible to determine a result because missing policies</li>
 * <li>NOTAPPLICABLE, existing policies does not permit to get a result for the request</li>
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
public enum XACMLResponseStatus {PERMIT,
    DENY,
    INDETERMINATE,
    NOTAPPLICABLE;
}
