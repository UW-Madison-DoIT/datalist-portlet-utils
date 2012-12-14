/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.ws.client.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.springframework.core.io.Resource;

/**
 * Wraps another {@link Resource}, delegates all calls to the the wrapped resource.
 * 
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
public class DelegatingResource implements Resource {
    protected final Resource resource;
    
    public DelegatingResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public boolean exists() {
        return resource.exists();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return resource.getInputStream();
    }

    @Override
    public boolean isReadable() {
        return resource.isReadable();
    }

    @Override
    public boolean isOpen() {
        return resource.isOpen();
    }

    @Override
    public URL getURL() throws IOException {
        return resource.getURL();
    }

    @Override
    public URI getURI() throws IOException {
        return resource.getURI();
    }

    @Override
    public File getFile() throws IOException {
        return resource.getFile();
    }

    @Override
    public long lastModified() throws IOException {
        return resource.lastModified();
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        return resource.createRelative(relativePath);
    }

    @Override
    public String getFilename() {
        return resource.getFilename();
    }

    @Override
    public String getDescription() {
        return resource.getDescription();
    }
    
    @Override
    public long contentLength() throws IOException {
        return resource.contentLength();
    }

    @Override
    public int hashCode() {
        return resource.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return resource.equals(obj);
    }

    @Override
    public String toString() {
        return resource.toString();
    }
}
