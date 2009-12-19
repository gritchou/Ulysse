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
package org.qualipso.factory.security.repository;

import javax.ejb.Local;


/**
 * A Service to give access to an underlying PolicyRepository.<br/>
 * <br/>
 * This internal service is not visible remotely and should only be used by trusted services. <br/>
 * <br/>
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 1 September 2009
 */
@Local
public interface PolicyRepositoryService {
    public static final String SERVICE_NAME = "policy-repository";

    /**
     * Get the policy repository instance.
     *
     * @return the PolicyRepository
     */
    public PolicyRepository getPolicyRepository();
}
