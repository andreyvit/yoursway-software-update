package com.yoursway.autoupdate.core;

import static com.yoursway.autoupdate.core.FileStateBuilder.buildActions;
import static com.yoursway.autoupdate.core.FileStateBuilder.modifiedFiles;
import static com.yoursway.autoupdate.core.internal.Activator.log;
import static com.yoursway.utils.YsFileUtils.saveToFile;
import static com.yoursway.utils.YsFileUtils.urlToFileWithProtocolCheck;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import org.eclipse.core.runtime.Platform;

import com.yoursway.autoupdate.core.checkres.CheckResult;
import com.yoursway.autoupdate.core.checkres.CommunicationErrorCheckResult;
import com.yoursway.autoupdate.core.checkres.InternalFailureCheckResult;
import com.yoursway.autoupdate.core.checkres.NoUpdatesCheckResult;
import com.yoursway.autoupdate.core.checkres.UpdateFoundCheckResult;
import com.yoursway.autoupdate.core.execution.RealExecutor42;
import com.yoursway.autoupdate.core.execution.RealExecutor9;
import com.yoursway.autoupdate.core.execution.RealReplaceTester;
import com.yoursway.autoupdate.core.versions.Version;
import com.yoursway.autoupdate.core.versions.definitions.IVersionDefinitionLoader;
import com.yoursway.autoupdate.core.versions.definitions.InvalidVersionDefinitionException;
import com.yoursway.autoupdate.core.versions.definitions.RemoteFile;
import com.yoursway.autoupdate.core.versions.definitions.UpdaterInfo;
import com.yoursway.autoupdate.core.versions.definitions.UrlBasedVersionDefinitionLoader;
import com.yoursway.autoupdate.core.versions.definitions.VersionDefinition;
import com.yoursway.autoupdate.core.versions.definitions.VersionDefinitionNotAvailable;
import com.yoursway.utils.StringInputStream;

public class AutomaticUpdater {
    
    private static final String PROPERTY_URL_OVERRIDE = "updater.url.override";
    
    private static final String PROPERTY_TESTS_PING_URL = "updater.tests.ping.url";
    
    public static void checkForUpdates(Version currentVersion, URL defaultUrl) throws UpdatesFoundExit {
        checkForUpdates(localInstallation(), currentVersion, defaultUrl);
    }
    
    public static CheckResult checkForUpdates1(Version currentVersion, URL updateUrl) {
        log("checkForUpdates is running for version " + currentVersion);
        updateUrl = determineUpdateUrl(updateUrl);
        IVersionDefinitionLoader loader = new UrlBasedVersionDefinitionLoader(updateUrl);
        try {
            VersionDefinition currentDef = loader.loadDefinition(currentVersion);
            if (!currentDef.hasNewerVersion())
                return new NoUpdatesCheckResult();
            
            Version freshVersion = currentDef.nextVersion();
            VersionDefinition freshDef = loader.loadDefinition(freshVersion);
            
            ProposedUpdateImpl update = new ProposedUpdateImpl(currentDef, freshDef);
            return new UpdateFoundCheckResult(update);
        } catch (VersionDefinitionNotAvailable e) {
            return new CommunicationErrorCheckResult();
        } catch (InvalidVersionDefinitionException e) {
            return new CommunicationErrorCheckResult();
        } catch (RuntimeException e) {
            return new InternalFailureCheckResult(e);
        } catch (Error e) {
            return new InternalFailureCheckResult(e);
        }
    }
    
    public static void doUpdate(ProposedUpdate update, InstallationProgressMonitor monitor)
            throws UpdatesFoundExit {
        doUpdate(localInstallation(), update, monitor);
    }

    private static ApplicationInstallation localInstallation() {
        return new ApplicationInstallation(urlToFileWithProtocolCheck(Platform
                .getInstallLocation().getURL()));
    }
    
    public static void doUpdate(ApplicationInstallation install, ProposedUpdate update,
            InstallationProgressMonitor monitor) throws UpdatesFoundExit {
        try {
            ProposedUpdateImpl updateImpl = (ProposedUpdateImpl) update;
            VersionDefinition freshDef = updateImpl.freshDef;
            
            Collection<RemoteFile> freshFiles = freshDef.files();
            
            Collection<FileAction> actions = buildActions(install.getFileContainer(), freshFiles);
            
            RealExecutor42 executor = new RealExecutor42();
            RealReplaceTester replaceTester = new RealReplaceTester();
            RealExecutor9 executor9 = new RealExecutor9();
            
            UpdaterInfo updaterInfo = freshDef.updaterInfo();
            UpdatePlanBuilder planBuilder = new UpdatePlanBuilder(replaceTester, modifiedFiles(actions)
                    .asCollection(), updaterInfo.files());
            UpdatePlan plan = planBuilder.build();
            try {
                saveToFile(new StringInputStream(plan.toString()), new File("/tmp/plan.txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ExecutablePlan executablePlan = plan.instantiate(new UpdateRequest(install.root(), install
                    .getFileContainer().allFiles(), actions, updaterInfo, executor9));
            try {
                executablePlan.execute(executor);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            
            throw new UpdatesFoundExit(true);
        } finally {
        }
    }
    
    public static void checkForUpdates(ApplicationInstallation install, Version currentVersion, URL updateUrl)
            throws UpdatesFoundExit {
        log("checkForUpdates is running for version " + currentVersion);
        if (!forcedUpdateCheckBecauseOfTests() && !shouldCheckForUpdates())
            return;
        updateUrl = determineUpdateUrl(updateUrl);
        IVersionDefinitionLoader loader = new UrlBasedVersionDefinitionLoader(updateUrl);
        try {
            VersionDefinition currentDef = loader.loadDefinition(currentVersion);
            if (!currentDef.hasNewerVersion()) {
                pingTestRunnerAndExitIfRequested();
                return;
            }
            
            Version freshVersion = currentDef.nextVersion();
            VersionDefinition freshDef = loader.loadDefinition(freshVersion);
            
            Collection<RemoteFile> freshFiles = freshDef.files();
            
            Collection<FileAction> actions = buildActions(install.getFileContainer(), freshFiles);
            
            RealExecutor42 executor = new RealExecutor42();
            RealReplaceTester replaceTester = new RealReplaceTester();
            RealExecutor9 executor9 = new RealExecutor9();
            
            UpdaterInfo updaterInfo = freshDef.updaterInfo();
            UpdatePlanBuilder planBuilder = new UpdatePlanBuilder(replaceTester, modifiedFiles(actions)
                    .asCollection(), updaterInfo.files());
            UpdatePlan plan = planBuilder.build();
            try {
                saveToFile(new StringInputStream(plan.toString()), new File("/tmp/plan.txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ExecutablePlan executablePlan = plan.instantiate(new UpdateRequest(install.root(), install
                    .getFileContainer().allFiles(), actions, updaterInfo, executor9));
            try {
                executablePlan.execute(executor);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            
            throw new UpdatesFoundExit(true);
        } catch (VersionDefinitionNotAvailable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (InvalidVersionDefinitionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    private static boolean shouldCheckForUpdates() {
        // TODO: once a day etc
        return true;
    }
    
    private static boolean forcedUpdateCheckBecauseOfTests() {
        return System.getProperty(PROPERTY_TESTS_PING_URL) != null;
    }
    
    private static void pingTestRunnerAndExitIfRequested() throws UpdatesFoundExit {
        String pingUrl = System.getProperty(PROPERTY_TESTS_PING_URL);
        if (pingUrl != null) {
            try {
                URL url = new URL(pingUrl);
                url.openStream().close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new UpdatesFoundExit(false);
        }
    }
    
    private static URL determineUpdateUrl(URL updateUrl) {
        String overrideUrl = System.getProperty(PROPERTY_URL_OVERRIDE);
        if (overrideUrl != null)
            try {
                log("Override update URL is " + overrideUrl);
                return new URL(overrideUrl);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        return updateUrl;
    }
    
}
