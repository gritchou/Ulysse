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
package org.qualipso.factory.core.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.sql.SQLException;

import javax.activation.DataSource;


/**
 * A FileDataSource object to allow reading it using DataHandler.
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 19 august 2009
 */
public class FileDataSource implements DataSource {
    private File file;

    public FileDataSource(File file) {
        this.file = file;
    }

    @Override
    public String getContentType() {
        return file.getContentType();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        try {
            return file.getBlob().getBinaryStream();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        try {
            return file.getBlob().setBinaryStream(1);
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}
