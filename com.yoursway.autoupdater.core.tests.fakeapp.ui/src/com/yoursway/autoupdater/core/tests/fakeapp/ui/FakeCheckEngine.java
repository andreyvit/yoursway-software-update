package com.yoursway.autoupdater.core.tests.fakeapp.ui;

import com.yoursway.autoupdate.core.CheckEngine;
import com.yoursway.autoupdate.core.checkres.CheckResult;
import com.yoursway.autoupdate.core.checkres.NoUpdatesCheckResult;

public class FakeCheckEngine implements CheckEngine {

    public CheckResult checkForUpdates() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        return new NoUpdatesCheckResult();
    }
    
}
