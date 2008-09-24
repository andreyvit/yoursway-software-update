package com.yoursway.autoupdater.core.localrepository;

public interface UpdatingListener {
    
    void downloadingStarted();
    
    void downloading(double progress);
    
    void downloadingCompleted();
    
    public static UpdatingListener NOP = new UpdatingListener() {
        public void downloading(double progress) {
        }
        
        public void downloadingCompleted() {
        }
        
        public void downloadingStarted() {
        }
    };
    
}
