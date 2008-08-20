package com.yoursway.autoupdater.filelibrary;

import java.io.File;
import java.net.URL;

public class LibraryFile {
    
    final URL url;
    final long size;
    
    private final File localFile;
    
    private long prevDoneSize;
    
    LibraryFile(URL url, long size, File localFile) {
        if (url == null)
            throw new NullPointerException("url is null");
        if (localFile == null)
            throw new NullPointerException("localFile is null");
        
        this.url = url;
        this.size = size;
        this.localFile = localFile;
    }
    
    FileState state() {
        long doneSize = doneSize();
        FileState state = new FileState(this, doneSize, prevDoneSize);
        prevDoneSize = doneSize;
        return state;
    }
    
    long doneSize() {
        return localFile.length();
    }
    
    boolean isDone() {
        return size == doneSize();
    }
    
    public File localFile() {
        return localFile;
    }
    
}
