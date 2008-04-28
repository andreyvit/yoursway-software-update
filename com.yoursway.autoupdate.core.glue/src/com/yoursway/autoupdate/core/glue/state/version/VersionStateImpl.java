package com.yoursway.autoupdate.core.glue.state.version;

import static com.yoursway.autoupdate.core.glue.ext.Clock.NEVER;
import static com.yoursway.autoupdate.core.glue.state.version.VersionDecision.INSTALLATION_FAILED;
import static com.yoursway.autoupdate.core.glue.state.version.VersionDecision.INSTALLING;
import static com.yoursway.autoupdate.core.glue.state.version.VersionDecision.POSTPONED;
import static com.yoursway.autoupdate.core.glue.state.version.VersionDecision.SKIPPED;
import static com.yoursway.autoupdate.core.glue.state.version.VersionDecision.UNDECIDED;
import static com.yoursway.utils.Listeners.newListenersByIdentity;

import com.yoursway.autoupdate.core.ProposedUpdate;
import com.yoursway.utils.Listeners;

public class VersionStateImpl {
    
    private ProposedUpdate update = null;
    
    private VersionDecision decision = null;
    
    private long decidedAt = NEVER;
    
    private transient Listeners<VersionStateListener> listeners = newListenersByIdentity();
    
    public VersionStateImpl() {
    }
    
    public VersionStateImpl(VersionStateMemento memento) {
        this.update = memento.update;
        this.decision = memento.decision;
        this.decidedAt = memento.decidedAt;
    }
    
    public synchronized void addListener(VersionStateListener listener) {
        listeners.add(listener);
    }
    
    public synchronized void removeListener(VersionStateListener listener) {
        listeners.remove(listener);
    }
    
    public synchronized void freshVersionFound(long now, ProposedUpdate update) {
        this.update = update;
        this.decision = VersionDecision.UNDECIDED;
        this.decidedAt = now;
        fireChanged(now);
    }
    
    public synchronized void skip(ProposedUpdate updateToSkip, long now) {
        if (update == null)
            throw new IllegalStateException("No update");
        if (update != updateToSkip)
            return; // silently ignore
        if (!decision.canSkipOrPostpone())
            throw new IllegalStateException("Invalid decision: " + decision);
        setDecision(now, SKIPPED);
    }
    
    public synchronized void postpone(ProposedUpdate updateToPostpone, long now) {
        if (update == null)
            throw new IllegalStateException("No update");
        if (update != updateToPostpone)
            return; // silently ignore
        if (!decision.canSkipOrPostpone())
            throw new IllegalStateException("Invalid decision: " + decision);
        setDecision(now, POSTPONED);
    }

    private void setDecision(long now, VersionDecision newDecision) {
        if (decision == newDecision)
            return;
        decision = newDecision;
        decidedAt = now;
        fireChanged(now);
    }
    
    public synchronized boolean install(ProposedUpdate updateToInstall, long now) {
        if (update == null)
            throw new IllegalStateException("No update");
        if (update != updateToInstall)
            return false;
        setDecision(now, INSTALLING);
        return true;
    }
    
    public synchronized void installationFailed(long now) {
        if (update == null)
            throw new IllegalStateException("No update");
        if (!decision.isInstalling())
            throw new IllegalStateException("Invalid decision: " + decision);
        setDecision(now, INSTALLATION_FAILED);
    }
    
    public synchronized void installationSucceeded(long now) {
        if (update == null)
            throw new IllegalStateException("No update");
        if (!decision.isInstalling())
            throw new IllegalStateException("Invalid decision: " + decision);
        update = null;
        decision = null;
        decidedAt = NEVER;
        fireChanged(now);
    }
    
    public synchronized ProposedUpdate getUndecidedUpdateIfExists() {
        if (update == null || decision != UNDECIDED)
            return null;
        return update;
    }
    
    private void fireChanged(long now) {
        for (VersionStateListener listener : listeners)
            listener.versionStateChanged(now);
    }
    
    public VersionStateMemento createMemento() {
        return new VersionStateMemento(update, decision, decidedAt);
    }

    public ProposedUpdate getUpdateToInstallIfExists() {
        if (update == null || decision != INSTALLING)
            return null;
        return update;
    }
    
}
