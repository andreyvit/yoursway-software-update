package com.yoursway.autoupdate.core.glue.state.overall;

import static com.yoursway.autoupdate.core.glue.ext.Clocks.isAfter;
import static com.yoursway.autoupdate.core.glue.state.overall.Mode.AUTOMATIC_CHECK;
import static com.yoursway.autoupdate.core.glue.state.overall.Mode.DISABLED;
import static com.yoursway.autoupdate.core.glue.state.overall.Mode.MANUAL_CHECK;
import static com.yoursway.autoupdate.core.glue.state.overall.Mode.NO_UPDATES;
import static com.yoursway.autoupdate.core.glue.state.overall.Mode.UPDATE_FOUND_ACTIONS_UNDECIDED;
import static com.yoursway.utils.Listeners.newListenersByIdentity;

import java.io.Serializable;

import com.yoursway.autoupdate.core.checkres.CheckResult;
import com.yoursway.autoupdate.core.checkres.ShutdownOccuredCheckResult;
import com.yoursway.autoupdate.core.glue.ext.Clocks;
import com.yoursway.utils.Listeners;

public class OverallStateImpl implements OverallState, Serializable, Cloneable {
    
    private static final long serialVersionUID = 1L;

    Mode mode = Mode.NO_UPDATES;
    
    private long startUpTime = -1;
    
    long firstRunTime = -1;
    
    private long lastSuccessfulCheckTime = -1;
    
    private long lastCheckAttempTime = -1;
    
    private long firstFailedCheckAfterLastSuccessfulCheckTime = -1;
    
    private transient Listeners<OverallStateListener> listeners = newListenersByIdentity();
    
    public OverallStateImpl() {
    }
    
    public OverallStateImpl(Serializable memento) {
        OverallStateImpl x = (OverallStateImpl) memento;
        mode = x.mode;
        startUpTime = x.startUpTime;
        firstRunTime = x.firstRunTime;
        lastSuccessfulCheckTime = x.lastSuccessfulCheckTime;
        lastCheckAttempTime = x.lastCheckAttempTime;
        firstFailedCheckAfterLastSuccessfulCheckTime = x.firstFailedCheckAfterLastSuccessfulCheckTime;
    }
    
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
        if (mode.isTemporary())
            mode = Mode.NO_UPDATES;
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
            if (result.updatesFound())
                mode = UPDATE_FOUND_ACTIONS_UNDECIDED;
            else
                mode = NO_UPDATES;
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
        return new Attempt(lastCheckAttempTime, Clocks.isNotConcrete(lastSuccessfulCheckTime)
                || isAfter(lastCheckAttempTime, lastSuccessfulCheckTime));
    }
    
    public synchronized Attempt lastSuccessfulCheckAttempt() {
        return new Attempt(lastSuccessfulCheckTime, true);
    }
    
    public synchronized Attempt firstFailedCheckAttempt() {
        return new Attempt(firstFailedCheckAfterLastSuccessfulCheckTime, false);
    }
    
    public synchronized long startUpTime() {
        return startUpTime;
    }
    
    private void notifyStateChanged(long now) {
        for (OverallStateListener listener : listeners)
            listener.overallStateChanged(now);
    }
    
    public Mode state() {
        return mode;
    }
    
    public long firstRunTime() {
        return firstRunTime;
    }
    
    public long lastCheckAttemptTime() {
        return lastCheckAttempTime;
    }
    
    public synchronized Serializable createMemento() {
        try {
            return (Serializable) clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public synchronized void startInstallation(long now) {
        this.mode = Mode.UPDATING;
        notifyStateChanged(now);
    }
    
    public synchronized void finishingInstallation(long now) {
        if (!mode.isUpdateInProgress())
            throw new IllegalStateException("Installation finish is not expected in state " + mode);
        this.mode = Mode.FINISHING_UPDATE;
        notifyStateChanged(now);
    }

    public void installationFinished(long now) {
        if (!mode.isUpdateInProgress())
            throw new IllegalStateException("Installation finish is not expected in state " + mode);
        mode = Mode.NO_UPDATES;
        notifyStateChanged(now);
    }
    
}
