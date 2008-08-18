package com.yoursway.autoupdater.filelibrary;

import static com.google.common.collect.Maps.newHashMap;

import java.net.URL;
import java.util.Iterator;
import java.util.Map;

class FileLibraryOrder implements Iterable<Request> {
    
    private final Map<URL, Request> requests = newHashMap();
    
    boolean contains(LibraryFile file) {
        return (requests.get(file.url) != null);
    }
    
    public Iterator<Request> iterator() {
        return requests.values().iterator();
    }
    
    void add(Request request) {
        requests.put(request.url, request);
    }
    
}
