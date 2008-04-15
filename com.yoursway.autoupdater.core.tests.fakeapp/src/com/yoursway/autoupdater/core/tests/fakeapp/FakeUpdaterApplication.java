package com.yoursway.autoupdater.core.tests.fakeapp;

import java.net.URL;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.Constants;

import com.yoursway.autoupdate.core.AutomaticUpdater;
import com.yoursway.autoupdate.core.UpdatesFoundExit;
import com.yoursway.autoupdate.core.versions.Version;

public class FakeUpdaterApplication implements IApplication {
    
    public Object start(IApplicationContext context) throws Exception {
        URL url = new URL("http://updates.yoursway.com/fakeapp/");
        String version = (String) Activator.getDefault().getBundle().getHeaders().get(
                Constants.BUNDLE_VERSION);
        Activator.log("Version of fakeapp: " + version);
        if (!version.startsWith("42."))
            try {
                Activator.log("Starting update check.");
                AutomaticUpdater.checkForUpdates(new Version("1.0"), url);
            } catch (UpdatesFoundExit e) {
                return IApplication.EXIT_RESTART;
            }
        AutomaticUpdater.notifyTestsThatNoUpdatesExist(url);
        return IApplication.EXIT_OK;
    }
    
    public void stop() {
    }
    
}
