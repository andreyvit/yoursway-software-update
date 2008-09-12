package com.yoursway.autoupdater.localrepository.internal;

import com.yoursway.autoupdater.auxiliary.AutoupdaterException;
import com.yoursway.autoupdater.filelibrary.FileLibraryListener;
import com.yoursway.autoupdater.filelibrary.LibrarySubscriber;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.LocalProductVersionMemento.State;

public interface ProductVersionState extends FileLibraryListener, LibrarySubscriber {
    
    void startUpdating();
    
    boolean updating();
    
    void continueWork();
    
    State toMementoState();
    
    void atStartup() throws AutoupdaterException;
    
    boolean failed();
    
}
