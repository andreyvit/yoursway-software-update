package com.yoursway.autoupdate.core;

import static com.yoursway.autoupdate.core.AutomaticUpdater.checkForUpdates1;

import java.net.URL;

import com.yoursway.autoupdate.core.checkres.CheckResult;
import com.yoursway.autoupdate.core.versions.Version;

public class UpdatesEngineImpl implements CheckEngine {
    
    private final Version currentVersion;
    private final URL updateUrl;

    public UpdatesEngineImpl(Version currentVersion, URL updateUrl) {
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

    public void update(ProposedUpdate update) {
    }
    
}
