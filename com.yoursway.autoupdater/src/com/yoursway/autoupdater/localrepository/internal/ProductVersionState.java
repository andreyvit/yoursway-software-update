package com.yoursway.autoupdater.localrepository.internal;

import java.util.Collection;

import com.yoursway.autoupdater.filelibrary.FileLibraryListener;
import com.yoursway.autoupdater.filelibrary.Request;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento;

public interface ProductVersionState extends FileLibraryListener {
    
    void startUpdating();
    
    boolean updating();
    
    void continueWork();
    
    ProductVersionStateMemento toMemento();
    
    Collection<Request> requiredFiles();
    
}
