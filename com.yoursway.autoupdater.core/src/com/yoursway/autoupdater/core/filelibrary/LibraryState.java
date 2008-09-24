package com.yoursway.autoupdater.core.filelibrary;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

public class LibraryState {
    
    private final Map<URL, FileState> fileStates = newHashMap();
    
    LibraryState(Collection<FileState> fileStates) {
        for (FileState state : fileStates)
            this.fileStates.put(state.url(), state);
    }
    
    public FileState stateOf(URL url) {
        return fileStates.get(url);
    }
    
    public boolean filesReady(Collection<Request> requests) {
        for (Request request : requests) {
            FileState state = stateOf(request.url());
            if (!state.isDone())
                return false;
        }
        return true;
    }
    
    public Collection<File> getLocalFiles(Collection<Request> requests) throws IOException, AssertionError {
        Collection<File> files = newLinkedList();
        for (Request request : requests) {
            FileState state = stateOf(request.url());
            
            /* //!
            if (!request.hash().equals(state.hash()))
                throw new AssertionError("Requested file is invalid and should be redownloaded before.");
            */

            files.add(state.getLocalFile());
        }
        return files;
    }
    
    public long totalBytes(Collection<Request> requests) {
        long size = 0;
        for (Request request : requests)
            size += request.size;
        return size;
    }
    
    public long localBytes(Collection<Request> requests) {
        long size = 0;
        for (Request request : requests) {
            FileState state = stateOf(request.url());
            size += state.doneSize;
        }
        return size;
    }
    
}
