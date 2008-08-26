package com.yoursway.autoupdater.filelibrary;

import java.util.Collection;


public interface LibrarySubscriber {
    
    Collection<Request> requiredFiles();
    
}
