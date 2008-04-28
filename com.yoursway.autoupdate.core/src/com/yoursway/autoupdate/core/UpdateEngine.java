package com.yoursway.autoupdate.core;

public interface UpdateEngine {
    
    void update(ProposedUpdate update, InstallationProgressMonitor progressMonitor);

    void cleanUpPreviousUpdate(InstallationProgressMonitor progressMonitor);
    
    boolean checkIfCleanupIsNeeded();
    
}
