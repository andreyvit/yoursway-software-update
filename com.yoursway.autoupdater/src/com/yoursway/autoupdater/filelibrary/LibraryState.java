package com.yoursway.autoupdater.filelibrary;

import static com.google.common.collect.Maps.newHashMap;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

public class LibraryState {
    
    private final Map<URL, FileState> states = newHashMap();
    
    LibraryState(Collection<FileState> states) {
        for (FileState state : states)
            this.states.put(state.url, state);
    }
    
    public FileState stateOf(URL url) {
        return states.get(url);
    }
    
    public boolean filesReady(Collection<Request> requests) {
        throw new UnsupportedOperationException();
    }
    
    public Collection<File> getLocalFiles(Collection<Request> requests) {
        throw new UnsupportedOperationException();
    }
    
}
