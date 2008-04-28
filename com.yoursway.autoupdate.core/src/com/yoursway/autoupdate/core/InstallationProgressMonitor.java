package com.yoursway.autoupdate.core;

public interface InstallationProgressMonitor {
    
    void starting();
    
    void downloading(long totalBytes);
    
    void downloadingProgress(long bytesDone);
    
    void installing();
    
    void finishing();
    
    void finished();
    
}
