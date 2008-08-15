package com.yoursway.autoupdater.filelibrary;

import java.util.LinkedList;
import java.util.List;

public class RequiredFiles {
    
    public final static RequiredFiles empty = new RequiredFiles();
    
    private final List<RequiredFile> files = new LinkedList<RequiredFile>();
    
    public Iterable<RequiredFile> files() {
        return files;
    }
    
    public int totalBytes() {
        int bytes = 0;
        for (RequiredFile file : files)
            bytes += file.filesize();
        return bytes;
    }
    
}
