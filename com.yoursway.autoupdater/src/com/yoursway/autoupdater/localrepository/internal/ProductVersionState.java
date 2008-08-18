package com.yoursway.autoupdater.localrepository.internal;

import com.yoursway.autoupdater.filelibrary.FileLibraryListener;
import com.yoursway.autoupdater.filelibrary.LibrarySubscriber;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento;

public interface ProductVersionState extends FileLibraryListener, LibrarySubscriber {
    
    void startUpdating();
    
    boolean updating();
    
    void continueWork();
    
    ProductVersionStateMemento toMemento();
    
}
