package com.yoursway.autoupdate.core.glue.state.overall;

import com.yoursway.autoupdate.core.checkres.CheckResult;

public interface OverallState {
    
    void addListener(OverallStateListener listener);
    
    void removeListener(OverallStateListener listener);
    
    void startup(long startUpTime);
    
    boolean startCheckingForUpdatesManually(long now);
    
    boolean startCheckingForUpdatesAutomatically(long now);
    
    void finishedCheckingForUpdates(long now, CheckResult result);
    
    void startInstallation(long now);
    
    void installationFinished(long now);

    long startUpTime();

    Attempt lastCheckAttempt();

    Mode state();

    void finishingInstallation(long now);
    
}