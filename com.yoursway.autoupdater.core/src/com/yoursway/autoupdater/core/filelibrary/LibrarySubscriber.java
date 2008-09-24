package com.yoursway.autoupdater.core.filelibrary;

import java.util.Collection;


public interface LibrarySubscriber {
    
    Collection<Request> libraryRequests();
    
}
