package com.yoursway.autoupdate.core;

import static com.yoursway.autoupdate.core.AutomaticUpdater.checkForUpdates1;

import java.net.URL;

import com.yoursway.autoupdate.core.checkres.CheckResult;
import com.yoursway.autoupdate.core.versions.Version;

public class RealCheckAndUpdateEngineImpl implements CheckEngine, UpdateEngine {
    
    private final Version currentVersion;
    private final URL updateUrl;

    public RealCheckAndUpdateEngineImpl(Version currentVersion, URL updateUrl) {
        if (currentVersion == null)
            throw new NullPointerException("currentVersion is null");
        if (updateUrl == null)
            throw new NullPointerException("updateUrl is null");
        this.currentVersion = currentVersion;
        this.updateUrl = updateUrl;
    }

    public CheckResult checkForUpdates() {
        return checkForUpdates1(currentVersion, updateUrl);
    }

    public VersionDescription currentVersion() {
        return null;
    }

    public boolean checkIfCleanupIsNeeded() {
        // TODO check if there is a pending update
        System.out.println("RealCheckAndUpdateEngineImpl.checkIfCleanupIsNeeded(): TODO check if there is a pending update");
        return false;
    }

    public void cleanUpPreviousUpdate(InstallationProgressMonitor progressMonitor) {
        // TODO check if there is a pending update, and either finish or roll it back
        System.out.println("RealCheckAndUpdateEngineImpl.cleanUpPreviousUpdate(): TODO check if there is a pending update, and either finish or roll it back");
    }

    public void update(ProposedUpdate update, InstallationProgressMonitor progressMonitor) {
        try {
            AutomaticUpdater.doUpdate(update, progressMonitor);
        } catch (UpdatesFoundExit e) {
            // TODO restart (do we ever reach this line?)
            System.out.println("RealCheckAndUpdateEngineImpl.update(): TODO restart (do we ever reach this line?)");
        }
    }
    
}
