package com.yoursway.autoupdater.filelibrary;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;

import java.io.File;
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
            FileState state = stateOf(request.url);
            if (!state.isDone())
                return false;
        }
        return true;
    }
    
    public Collection<File> getLocalFiles(Collection<Request> requests) {
        Collection<File> files = newLinkedList();
        for (Request request : requests) {
            FileState state = stateOf(request.url);
            files.add(state.getLocalFile());
        }
        return files;
    }
    
}
