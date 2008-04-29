package com.yoursway.autoupdate.core.glue;

import com.yoursway.autoupdate.core.InstallationProgressMonitor;
import com.yoursway.autoupdate.core.ProposedUpdate;
import com.yoursway.autoupdate.core.UpdateEngine;
import com.yoursway.autoupdate.core.glue.internal.Activator;
import com.yoursway.autoupdate.core.glue.state.overall.OverallState;
import com.yoursway.autoupdate.core.glue.state.version.VersionStateImpl;
import com.yoursway.autoupdate.core.glue.state.version.VersionStateListener;

public class UpdateController implements VersionStateListener {
    
    private final UpdateEngine updateEngine;
    private final VersionStateImpl versionState;
    private final ExecutorWithTime executor;
    
    private ProposedUpdate updateBeingInstalled = null;
    private final OverallState overallState;
    
    private MulticastingInstallationProgressMonitor multicastMonitor = new MulticastingInstallationProgressMonitor();
    
    public UpdateController(UpdateEngine updateEngine, VersionStateImpl versionState,
            OverallState overallState, ExecutorWithTime executor) {
        if (updateEngine == null)
            throw new NullPointerException("updateEngine is null");
        if (versionState == null)
            throw new NullPointerException("versionState is null");
        if (overallState == null)
            throw new NullPointerException("overallState is null");
        if (executor == null)
            throw new NullPointerException("executor is null");
        this.updateEngine = updateEngine;
        this.versionState = versionState;
        this.overallState = overallState;
        this.executor = executor;
        versionState.addListener(this);
    }
    
    public void cleanupAfterUpdating(long now) {
        if (overallState.state().isUpdateInProgress())
            doCleanupAfterUpdating(now);
    }
    
    private synchronized void doCleanupAfterUpdating(long now) {
        overallState.finishingInstallation(now);
        executor.execute(new Runnable() {
            
            public void run() {
                updateEngine.cleanUpPreviousUpdate(multicastMonitor);
            }
            
        }, new RunnableWithTime() {
            
            public void run(long now) {
                finishCleanupAfterUpdating(now);
            }
            
        });
    }
    
    private synchronized void finishCleanupAfterUpdating(long now) {
        overallState.installationFinished(now);
    }
    
    public synchronized void versionStateChanged(long now) {
        ProposedUpdate update = versionState.getUpdateToInstallIfExists();
        if (update != null && updateBeingInstalled == null) {
            updateBeingInstalled = update;
            executor.execute(new Runnable() {
                
                public void run() {
                    doInstallUpdate();
                }
                
            }, new RunnableWithTime() {
                
                public void run(long now) {
                    finishedInstallingUpdate(now);
                }
                
            });
        }
    }
    
    void doInstallUpdate() {
        multicastMonitor.starting();
        try {
            updateEngine.update(updateBeingInstalled, multicastMonitor);
        } catch (Throwable e) {
            Activator.log("Update process failed", e);
        }
    }
    
    private synchronized void finishedInstallingUpdate(long now) {
        updateBeingInstalled = null;
        versionState.installationSucceeded(now);
        overallState.installationFinished(now);
    }
    
    public void addMonitor(InstallationProgressMonitor monitor) {
        multicastMonitor.addMonitor(monitor);
    }
    
    public void removeMonitor(InstallationProgressMonitor monitor) {
        multicastMonitor.removeMonitor(monitor);
    }
    
}
