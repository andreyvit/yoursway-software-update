package com.yoursway.autoupdater.filelibrary;

import com.yoursway.utils.EventSource;

public interface FileLibrary {
    
    public abstract void order(FileLibraryOrder order);
    
    public abstract EventSource<FileLibraryListener> events();
    
    public abstract OrderManager orderManager();
    
}