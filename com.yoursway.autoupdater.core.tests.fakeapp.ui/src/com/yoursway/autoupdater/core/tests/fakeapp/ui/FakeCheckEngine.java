package com.yoursway.autoupdater.core.tests.fakeapp.ui;

import com.yoursway.autoupdate.core.CheckEngine;
import com.yoursway.autoupdate.core.checkres.CheckResult;
import com.yoursway.autoupdate.core.checkres.UpdateFoundCheckResult;

public class FakeCheckEngine implements CheckEngine {
    
    public CheckResult checkForUpdates() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        //        return new NoUpdatesCheckResult();
        return new UpdateFoundCheckResult(new FakeProposedUpdate("0.2-mac", "0.2", "<h1>Version 0.2</h1>\n"
                + "<p>Everything has been thrown away and rewritten from scratch.</p>\n"
                + "<p>Major improvements:\n" + "<ul>" + "<li>ÄÄDebugging, breakpoints etc</li>"
                + "<li>Step Into / Step Over" + "<li>Automatic Updater included" + "</ul>"));
    }
    
}
