package com.yoursway.autoupdater.internal;

public interface ProductVersionState {
    
    void startUpdating();
    
    boolean updating();

    void continueWork();
    
}
