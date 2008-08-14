package com.yoursway.autoupdater.localrepository.internal;

import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento;

public interface ProductVersionState {
    
    void startUpdating();
    
    boolean updating();
    
    void continueWork();
    
    ProductVersionStateMemento toMemento();
    
    boolean isCurrent();
    
}