package com.yoursway.autoupdater.filelibrary;

import static com.google.common.collect.Maps.newHashMap;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

public class LibraryState {
    
    private final Map<URL, FileState> fileStates = newHashMap();
    
    LibraryState(Collection<FileState> fileStates) {
        for (FileState state : fileStates)
            this.fileStates.put(state.url, state);
    }
    
    public LibraryFile stateOf(URL url) {
        return fileStates.get(url);
    }
    
    public boolean filesReady(Collection<Request> requests) {
        throw new UnsupportedOperationException();
    }
    
    public Collection<File> getLocalFiles(Collection<Request> requests) {
        throw new UnsupportedOperationException();
    }
    
}
