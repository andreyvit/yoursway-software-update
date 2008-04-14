package com.yoursway.autoupdater.core.tests.fakeapp;

import java.net.URL;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import com.yoursway.autoupdate.core.AutomaticUpdater;
import com.yoursway.autoupdate.core.UpdatesFoundExit;
import com.yoursway.autoupdate.core.versions.Version;

public class FakeUpdaterApplication implements IApplication {
    
    public Object start(IApplicationContext context) throws Exception {
        try {
            AutomaticUpdater.checkForUpdates(new Version("1.0"), new URL(
                    "http://update.yoursway.com/fakeapp/"));
        } catch (UpdatesFoundExit e) {
        }
        return null;
    }
    
    public void stop() {
    }
    
}
