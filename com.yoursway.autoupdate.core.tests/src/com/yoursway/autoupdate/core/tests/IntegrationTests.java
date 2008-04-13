package com.yoursway.autoupdate.core.tests;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.update.configurator.ConfiguratorUtils;
import org.eclipse.update.configurator.IPlatformConfiguration;
import org.eclipse.update.configurator.IPlatformConfiguration.ISiteEntry;
import org.junit.Test;
import org.osgi.framework.Bundle;

import com.yoursway.autoupdate.core.ApplicationInstallation;
import com.yoursway.autoupdate.core.AutomaticUpdater;
import com.yoursway.autoupdate.core.UpdatesFoundExit;
import com.yoursway.autoupdate.core.app.layout.MacBundlePlatformLayout;
import com.yoursway.autoupdate.core.app.layout.PlatformLayout;
import com.yoursway.autoupdate.core.tests.internal.Activator;
import com.yoursway.autoupdate.core.tests.internal.SimpleHttpServer;
import com.yoursway.autoupdate.core.tests.internal.SimpleServlet;
import com.yoursway.autoupdate.core.tests.layouts.CurrentPlatformSource;
import com.yoursway.autoupdate.core.tests.layouts.WritableMacBundleLayout;
import com.yoursway.autoupdate.core.versions.Version;
import com.yoursway.utils.YsFileUtils;

public class IntegrationTests {
    
    @Test
    public void fooChanged() throws IOException, InterruptedException {
        
        Bundle[] bundles = Activator.getContext().getBundles();
        for (Bundle b : bundles) {
            String location = b.getLocation();
            System.out.println("Bundle " + b.getSymbolicName() + " at " + location);
            
        }
        
        IPlatformConfiguration config = ConfiguratorUtils.getCurrentPlatformConfiguration();
        ISiteEntry[] sites = config.getConfiguredSites();
        for (ISiteEntry site : sites) {
            System.out.println(site);
        }
        
        WebServer webServer = new WebServer();
        
        try {
            File root = YsFileUtils.createTempFolder("autoupdater", "tests");
            
            File platformLocation = new File(Platform.getInstallLocation().getURL().getPath());
            
            PlatformLayout layout = ApplicationInstallation.determineLayout(platformLocation);
            
            File appRoot = new File(Activator.getDefault().getStateLocation().toFile(), "Fake.app");
            YsFileUtils.deleteRecursively(appRoot);
            
            WritableMacBundleLayout lll = new WritableMacBundleLayout(appRoot, new CurrentPlatformSource());
            // platform
            lll.copyPlugin("javax.servlet");
            lll.copyPlugin("org.eclipse.core.contenttype");
            lll.copyPlugin("org.eclipse.core.jobs");
            lll.copyPlugin("org.eclipse.core.runtime");
            lll.copyPlugin("org.eclipse.equinox.app");
            lll.copyPlugin("org.eclipse.equinox.common");
            lll.copyPlugin("org.eclipse.equinox.preferences");
            lll.copyPlugin("org.eclipse.equinox.registry");
            lll.copyPlugin("org.eclipse.osgi");
            lll.copyPlugin("org.eclipse.osgi.services");
            lll.copyPlugin("org.eclipse.update.configurator");
            lll.copyJar("org.eclipse.equinox.launcher");
            lll.copyJar("org.eclipse.equinox.launcher.carbon.macosx");
            // app
            lll.copyPlugin("com.google.collections");
            lll.copyPlugin("com.yoursway.autoupdate.core");
            lll.copyPlugin("com.yoursway.autoupdate.core.actions");
            lll.copyPlugin("com.yoursway.autoupdater.core.tests.fakeapp");
            lll.copyPlugin("com.yoursway.utils");
            
            MacBundlePlatformLayout layyy = lll.toLayout();
            
            System.out.println("appRoot = " + appRoot);
            System.out.println(platformLocation);
            
            try {
                AutomaticUpdater.checkForUpdates(new Version("1.0"),
                        new URL("http://localhost:" + webServer.getPort() + "/"));
                fail("No updates found.");
            } catch (UpdatesFoundExit e) {
            }
            
            layout.createRunnableUpdaterAt(root);
        } finally {
            webServer.dispose();
        }
        
    }
    
}
