package com.yoursway.autoupdate.core;

import java.io.File;

import org.eclipse.core.runtime.Assert;

public class FilePair {

    private final File source;
    private final File destination;

    public FilePair(File source, File destination) {
        Assert.isNotNull(source);
        Assert.isNotNull(destination);
        this.source = source;
        this.destination = destination;
    }
    
    public File source() {
        return source;
    }
    
    public File destination() {
        return destination;
    }
    
}
