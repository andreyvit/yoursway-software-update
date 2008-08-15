package com.yoursway.autoupdater.localrepository.internal;

import com.yoursway.autoupdater.filelibrary.FileLibraryListener;
import com.yoursway.autoupdater.filelibrary.RequiredFiles;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento;

public interface ProductVersionState extends FileLibraryListener {
    
    void startUpdating();
    
    boolean updating();
    
    void continueWork();
    
    ProductVersionStateMemento toMemento();
    
    RequiredFiles requiredFiles();
    
}
