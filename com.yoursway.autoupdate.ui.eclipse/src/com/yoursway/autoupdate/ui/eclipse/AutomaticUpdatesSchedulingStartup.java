package com.yoursway.autoupdate.ui.eclipse;

import static com.yoursway.autoupdate.ui.eclipse.internal.Activator.PREFERENCE_PAGE_ID;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.yoursway.autoupdate.core.CheckEngine;
import com.yoursway.autoupdate.core.RealCheckAndUpdateEngineImpl;
import com.yoursway.autoupdate.core.UpdateEngine;
import com.yoursway.autoupdate.core.glue.GlueIntegrator;
import com.yoursway.autoupdate.core.glue.GlueIntegratorImpl;
import com.yoursway.autoupdate.core.glue.persister.Storage;
import com.yoursway.autoupdate.core.glue.persister.TransactionalStorage;
import com.yoursway.autoupdate.core.versions.Version;
import com.yoursway.autoupdate.ui.GlueToDialog;
import com.yoursway.autoupdate.ui.GlueToPreferences;
import com.yoursway.autoupdate.ui.SwtRelativeScheduler;
import com.yoursway.autoupdate.ui.SystemClock;
import com.yoursway.autoupdate.ui.UpdatePreferencesComposite;
import com.yoursway.autoupdate.ui.eclipse.internal.Activator;
import com.yoursway.autoupdater.fake.FakeCheckEngine;
import com.yoursway.autoupdater.fake.FakeUpdateEngine;

public class AutomaticUpdatesSchedulingStartup implements IStartup, RcpAutomaticUpdater {
    
    private static AutomaticUpdatesSchedulingStartup instance;
    
    private GlueToPreferences glueToPreferences;
    
    private GlueIntegrator glue;
    
    public static class UpdateInfo {
        
        private final URL updateUrl;
        private final Version versionId;
        private final String versionDisplayName;
        
        public UpdateInfo(URL updateUrl, Version versionId, String versionDisplayName) {
            if (updateUrl == null)
                throw new NullPointerException("updateUrl is null");
            if (versionId == null)
                throw new NullPointerException("versionId is null");
            if (versionDisplayName == null)
                throw new NullPointerException("versionDisplayName is null");
            this.updateUrl = updateUrl;
            this.versionId = versionId;
            this.versionDisplayName = versionDisplayName;
        }
        
        public URL updateUrl() {
            return updateUrl;
        }
        
        public Version version() {
            return versionId;
        }
        
        public String versionDisplayName() {
            return versionDisplayName;
        }
        
    }
    
    public void earlyStartup() {
        instance = this;
        try {
            File stateDir = new File(Activator.getDefault().getStateLocation().toFile(), "updater");
            Storage storage = new TransactionalStorage(new File(stateDir, "state.bin"), new File(stateDir,
                    "state.upd"));
            Executor executor = new ThreadPoolExecutor(0, 1, 10000, TimeUnit.MILLISECONDS,
                    new SynchronousQueue<Runnable>());
            
            UpdateInfo updateInfo = determineUpdateInfo();
            
            RealCheckAndUpdateEngineImpl realEngine = new RealCheckAndUpdateEngineImpl(updateInfo.version(),
                    updateInfo.updateUrl());
            boolean useTheRealThing = true;
            CheckEngine checkEngine = (useTheRealThing ? realEngine : new FakeCheckEngine());
            UpdateEngine updateEngine = (useTheRealThing ? realEngine : new FakeUpdateEngine());
            
            Display display = Display.getDefault();
            glue = new GlueIntegratorImpl(new SystemClock(), checkEngine, updateEngine, executor,
                    new SwtRelativeScheduler(display), storage);
            
            glueToPreferences = new GlueToPreferences(glue, display) {
                
                @Override
                protected void showPreferencesComposite() {
                    openPreferencePage();
                }
                
            };
            new GlueToDialog(glue, Activator.getDefault().getDialogSettings());
            
            //        UpdatePreferencesComposite prefs = new UpdatePreferencesComposite(shell, SWT.NONE);
            //        glueToPreferences.hook(prefs);
        } catch (IOException e) {
            Activator.getDefault().getLog().log(
                    new Status(Status.ERROR, Activator.PLUGIN_ID,
                            "Automatic Updates are disabled because of an error while initializing", e));
        }
    }
    
    private UpdateInfo determineUpdateInfo() {
        IExtensionPoint ep = Platform.getExtensionRegistry().getExtensionPoint(
                "com.yoursway.autoupdate.ui.eclipse.updatableApplications");
        for (IExtension extension : ep.getExtensions()) {
            for (IConfigurationElement element : extension.getConfigurationElements()) {
                if ("application".equals(element.getName())) {
                    try {
                        String updateUrlString = getRequiredAttr(element, "update-url");
                        URL updateUrl = new URL(updateUrlString);
                        String version = getRequiredAttr(element, "version");
                        String versionName = getRequiredAttr(element, "version-name");
                        return new UpdateInfo(updateUrl, new Version(version), versionName);
                    } catch (MalformedURLException e) {
                    }
                } else {
                    throw new RuntimeException("Invalid element: " + element.getName());
                }
            }
        }
        throw new RuntimeException("No updatable applications found.");
    }
    
    private static String getRequiredAttr(IConfigurationElement element, String attrName) {
        String value = element.getAttribute(attrName);
        if (value == null || (value = value.trim()).length() == 0)
            throw new RuntimeException("Missing " + attrName);
        return value;
    }
    
    public static RcpAutomaticUpdater instance() {
        if (instance == null)
            throw new IllegalStateException("The instance has not yet been created");
        return instance;
    }
    
    public void checkForUpdates() {
        glue.checkForUpdates();
        openPreferencePage();
    }
    
    private void openPreferencePage() {
        Display.getDefault().asyncExec(new Runnable() {
            
            public void run() {
                PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(null, PREFERENCE_PAGE_ID,
                        null, null);
                dialog.setBlockOnOpen(false);
                dialog.open();
            }
            
        });
    }
    
    public void add(final UpdatePreferencesComposite composite) {
        glueToPreferences.hook(composite);
        composite.addDisposeListener(new DisposeListener() {
            
            public void widgetDisposed(DisposeEvent e) {
                glueToPreferences.unhook(composite);
            }
            
        });
    }
    
}
