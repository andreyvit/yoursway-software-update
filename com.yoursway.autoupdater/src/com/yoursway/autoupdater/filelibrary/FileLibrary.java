package com.yoursway.autoupdater.filelibrary;

import com.yoursway.utils.EventSource;

public interface FileLibrary {
    
    void order(FileLibraryOrder order);
    
    EventSource<FileLibraryListener> events();
    
    OrderManager orderManager();
    
}