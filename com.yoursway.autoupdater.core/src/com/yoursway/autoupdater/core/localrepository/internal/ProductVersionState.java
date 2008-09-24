package com.yoursway.autoupdater.core.localrepository.internal;

import com.yoursway.autoupdater.core.filelibrary.FileLibraryListener;
import com.yoursway.autoupdater.core.filelibrary.LibrarySubscriber;
import com.yoursway.autoupdater.core.protos.LocalRepositoryProtos.LocalProductVersionMemento.State;

public interface ProductVersionState extends FileLibraryListener, LibrarySubscriber {
    
    void startUpdating();
    
    boolean updating();
    
    void continueWork();
    
    State toMementoState();
    
    void atStartup();
    
    boolean failed();
    
}
