package com.yoursway.autoupdater.filelibrary;

import static com.google.common.collect.Maps.newHashMap;

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

class FileLibraryOrder implements Iterable<Request> {
    
    private final Map<URL, Request> requests = newHashMap();
    
    public FileLibraryOrder() {
        // nothing
    }

    public FileLibraryOrder(Collection<Request> requests) {
        add(requests);
    }
    
    boolean contains(LibraryFile file) {
        return (requests.get(file.url) != null);
    }
    
    public Iterator<Request> iterator() {
        return requests.values().iterator();
    }
    
    void add(Request request) {
        requests.put(request.url, request);
    }
    
    void add(Collection<Request> requests) {
        for (Request request : requests)
            add(request);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FileLibraryOrder) {
            if (obj == this)
                return true;
            
            FileLibraryOrder flo = (FileLibraryOrder) obj;
            return requests.equals(flo.requests);
        }
        return super.equals(obj);
    }
}
