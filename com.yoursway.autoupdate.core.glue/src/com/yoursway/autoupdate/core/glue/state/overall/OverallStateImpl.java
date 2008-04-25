package com.yoursway.autoupdate.core.glue.state.overall;

import static com.yoursway.autoupdate.core.glue.ext.Clocks.isAfter;
import static com.yoursway.autoupdate.core.glue.state.overall.Mode.AUTOMATIC_CHECK;
import static com.yoursway.autoupdate.core.glue.state.overall.Mode.DISABLED;
import static com.yoursway.autoupdate.core.glue.state.overall.Mode.MANUAL_CHECK;
import static com.yoursway.autoupdate.core.glue.state.overall.Mode.NO_UPDATES;
import static com.yoursway.autoupdate.core.glue.state.overall.Mode.UPDATE_FOUND_ACTIONS_UNDECIDED;
import static com.yoursway.utils.Listeners.newListenersByIdentity;

import com.yoursway.autoupdate.core.glue.checkres.CheckResult;
import com.yoursway.autoupdate.core.glue.checkres.ShutdownOccuredCheckResult;
import com.yoursway.utils.Listeners;

public class OverallStateImpl implements OverallState {
    
    private Mode mode = Mode.NO_UPDATES;
    
    private long startUpTime = -1;
    
    private long firstRunTime = -1;
    
    private long lastSuccessfulCheckTime = -1;
    
    private long lastCheckAttempTime = -1;
    
    private long firstFailedCheckAfterLastSuccessfulCheckTime = -1;
    
    private transient Listeners<OverallStateListener> listeners = newListenersByIdentity();
    
    public synchronized void addListener(OverallStateListener listener) {
        listeners.add(listener);
    }
    
    public synchronized void removeListener(OverallStateListener listener) {
        listeners.remove(listener);
    }
    
    public synchronized void startup(long startUpTime) {
        this.startUpTime = startUpTime;
        if (firstRunTime < 0)
            firstRunTime = startUpTime;
        if (mode.isExpectingUpdateCheckResult())
            finishedCheckingForUpdates(startUpTime, new ShutdownOccuredCheckResult());
    }
    
    public synchronized boolean startCheckingForUpdatesManually(long now) {
        if (!mode.canStartManualCheckingForUpdates())
            return false;
        mode = MANUAL_CHECK;
        lastCheckAttempTime = now;
        notifyStateChanged(now);
        return true;
    }
    
    public synchronized boolean startCheckingForUpdatesAutomatically(long now) {
        if (!mode.canStartAutomaticCheckingForUpdates())
            return false;
        mode = AUTOMATIC_CHECK;
        lastCheckAttempTime = now;
        notifyStateChanged(now);
        return true;
    }
    
    public synchronized void finishedCheckingForUpdates(long now, CheckResult result) {
        if (!mode.isExpectingUpdateCheckResult())
            throw new IllegalStateException("No check result is expected in state " + mode);
        if (result.isSuccess()) {
            lastSuccessfulCheckTime = lastCheckAttempTime;
            firstFailedCheckAfterLastSuccessfulCheckTime = -1;
            mode = UPDATE_FOUND_ACTIONS_UNDECIDED;
        } else {
            if (firstFailedCheckAfterLastSuccessfulCheckTime < 0)
                firstFailedCheckAfterLastSuccessfulCheckTime = lastCheckAttempTime;
            if (result.isNoWriteAccessResult())
                mode = DISABLED;
            else
                mode = NO_UPDATES;
        }
        notifyStateChanged(now);
    }
    
    public synchronized Attempt lastCheckAttempt() {
        return new Attempt(lastCheckAttempTime, isAfter(lastCheckAttempTime, lastSuccessfulCheckTime));
    }
    
    public synchronized long startUpTime() {
        return startUpTime;
    }
    
    private void notifyStateChanged(long now) {
        for (OverallStateListener listener : listeners)
            listener.overallStateChanged(now);
    }
    
}
