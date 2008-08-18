package com.yoursway.autoupdater.filelibrary;

import java.io.File;
import java.net.URL;

import com.yoursway.utils.annotations.Immutable;

@Immutable
public class FileState extends LibraryFile {
    
    private final long doneSize;
    
    FileState(URL url, long size, long doneSize, File localFile) {
        super(url, size, localFile);
        this.doneSize = doneSize;
    }
    
    @Override
    long doneSize() {
        return doneSize;
    }
    
}
