package com.yoursway.autoupdater.filelibrary;

import java.io.File;
import java.net.URL;

import com.yoursway.utils.annotations.Immutable;

@Immutable
public class FileState {
    
    private final LibraryFile file;
    private final long doneSize;
    private final long prevDoneSize;
    
    FileState(LibraryFile file, long doneSize, long prevDoneSize) {
        this.file = file;
        this.doneSize = doneSize;
        this.prevDoneSize = prevDoneSize;
    }
    
    URL url() {
        return file.url;
    }
    
    boolean isDone() {
        return file.size == doneSize;
    }
    
    double progress() {
        return file.size == 0 ? 1.0 : (doneSize * 1.0 / file.size);
    }
    
    File getLocalFile() {
        if (!isDone())
            throw new IllegalStateException("The file has not yet been downloaded.");
        
        return file.localFile();
    }
    
    boolean significantlyChanged() {
        if (isDone() && prevDoneSize != file.size)
            return true;
        return (doneSize - prevDoneSize >= file.size / 100.0);
    }
}
