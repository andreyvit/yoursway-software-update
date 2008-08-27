package com.yoursway.autoupdater.localrepository;

public interface UpdatingListener {
    
    public static UpdatingListener EMPTY = new EmptyUpdatingListener();
    
    void downloadingStarted();
    
    void downloading(double progress);
    
    void downloadingCompleted();
    
}
