package com.yoursway.autoupdater.filelibrary;

import java.io.File;
import java.net.URL;

class LibraryFile {
    
    final URL url;
    private final long size;
    
    final File localFile;
    
    LibraryFile(URL url, long size, File localFile) {
        if (url == null)
            throw new NullPointerException("url is null");
        if (localFile == null)
            throw new NullPointerException("localFile is null");
        
        this.url = url;
        this.size = size;
        this.localFile = localFile;
    }
    
    LibraryFile state() {
        return new FileState(url, size, doneSize(), localFile);
    }
    
    long doneSize() {
        return localFile.length();
    }
    
    public boolean isDone() {
        return size == doneSize();
    }
    
    public double progress() {
        return size == 0 ? 1.0 : (doneSize() * 1.0 / size);
    }
    
    public File getLocalFile() {
        if (!isDone())
            throw new IllegalStateException("The file has not yet been downloaded.");
        
        return localFile;
    }
    
}
