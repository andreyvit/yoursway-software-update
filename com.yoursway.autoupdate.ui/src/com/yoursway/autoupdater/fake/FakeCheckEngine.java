package com.yoursway.autoupdater.fake;

import com.yoursway.autoupdate.core.CheckEngine;
import com.yoursway.autoupdate.core.VersionDescription;
import com.yoursway.autoupdate.core.checkres.CheckResult;
import com.yoursway.autoupdate.core.checkres.UpdateFoundCheckResult;
import com.yoursway.autoupdate.core.versions.Version;

public class FakeCheckEngine implements CheckEngine {
    
    public CheckResult checkForUpdates() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        //        return new NoUpdatesCheckResult();
        return new UpdateFoundCheckResult(new FakeProposedUpdate("0.2-mac", "0.2", "<h1>Version 0.2</h1>\n"
                + "<p>Everything has been thrown away and rewritten from scratch.</p>\n"
                + "<p>Major improvements:\n" + "<ul>" + "<li>Debugging, breakpoints etc</li>"
                + "<li>Step Into / Step Over" + "<li>Automatic Updater included" + "</ul>"));
    }

    public VersionDescription currentVersion() {
        return new VersionDescription() {

            public String displayName() {
                return "0.1";
            }

            public Version version() {
                return new Version("0.1-mac");
            }
            
        };
    }
    
}
