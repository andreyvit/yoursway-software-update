package com.yoursway.autoupdate.core;

import static com.yoursway.autoupdate.core.AutomaticUpdater.determineUpdateUrl;
import static com.yoursway.autoupdate.core.internal.Activator.logInfo;

import java.net.URL;

import com.yoursway.autoupdate.core.checkres.CheckResult;
import com.yoursway.autoupdate.core.checkres.CommunicationErrorCheckResult;
import com.yoursway.autoupdate.core.checkres.InternalFailureCheckResult;
import com.yoursway.autoupdate.core.checkres.NoUpdatesCheckResult;
import com.yoursway.autoupdate.core.checkres.UpdateFoundCheckResult;
import com.yoursway.autoupdate.core.versions.Version;
import com.yoursway.autoupdate.core.versions.definitions.IVersionDefinitionLoader;
import com.yoursway.autoupdate.core.versions.definitions.InvalidVersionDefinitionException;
import com.yoursway.autoupdate.core.versions.definitions.UrlBasedVersionDefinitionLoader;
import com.yoursway.autoupdate.core.versions.definitions.VersionDefinition;
import com.yoursway.autoupdate.core.versions.definitions.VersionDefinitionNotAvailable;

public class RealCheckAndUpdateEngineImpl implements CheckEngine, UpdateEngine {
    
    private final Version currentVersion;
    private final URL updateUrl;
    private VersionDefinition currentDef;
    
    public RealCheckAndUpdateEngineImpl(Version currentVersion, URL updateUrl) {
        if (currentVersion == null)
            throw new NullPointerException("currentVersion is null");
        if (updateUrl == null)
            throw new NullPointerException("updateUrl is null");
        this.currentVersion = currentVersion;
        this.updateUrl = determineUpdateUrl(updateUrl);
    }
    
    public CheckResult checkForUpdates() {
        return doCheckForUpdates();
    }
    
    private CheckResult doCheckForUpdates() {
        logInfo(("checkForUpdates is running for version " + currentVersion));
        IVersionDefinitionLoader loader = new UrlBasedVersionDefinitionLoader(updateUrl);
        try {
            currentDef = loader.loadDefinition(currentVersion);
            if (!currentDef.hasNewerVersion())
                return new NoUpdatesCheckResult();
            
            Version freshVersion = currentDef.nextVersion();
            VersionDefinition freshDef = loader.loadDefinition(freshVersion);
            
            ProposedUpdateImpl update = new ProposedUpdateImpl(currentDef, freshDef);
            return new UpdateFoundCheckResult(update);
        } catch (VersionDefinitionNotAvailable e) {
            return new CommunicationErrorCheckResult(e);
        } catch (InvalidVersionDefinitionException e) {
            return new CommunicationErrorCheckResult(e);
        } catch (RuntimeException e) {
            return new InternalFailureCheckResult(e);
        } catch (Error e) {
            return new InternalFailureCheckResult(e);
        }
    }
    
    public VersionDescription currentVersion() {
        if (currentDef == null)
            return new VersionDescription() {
                
                public String displayName() {
                    return currentVersion.versionString();
                }
                
                public Version version() {
                    return currentVersion;
                }
                
            };
        return currentDef;
    }
    
    public boolean checkIfCleanupIsNeeded() {
        // TODO check if there is a pending update
        System.out
                .println("RealCheckAndUpdateEngineImpl.checkIfCleanupIsNeeded(): TODO check if there is a pending update");
        return false;
    }
    
    public void cleanUpPreviousUpdate(InstallationProgressMonitor progressMonitor) {
        // TODO check if there is a pending update, and either finish or roll it back
        System.out
                .println("RealCheckAndUpdateEngineImpl.cleanUpPreviousUpdate(): TODO check if there is a pending update, and either finish or roll it back");
    }
    
    public void update(ProposedUpdate update, InstallationProgressMonitor progressMonitor) {
        try {
            AutomaticUpdater.doUpdate(update, progressMonitor);
        } catch (UpdatesFoundExit e) {
            // TODO restart (do we ever reach this line?)
            System.out
                    .println("RealCheckAndUpdateEngineImpl.update(): TODO restart (do we ever reach this line?)");
        }
    }
    
}
