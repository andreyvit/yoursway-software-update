package com.yoursway.autoupdate.core.glue.unused;

import com.yoursway.autoupdate.core.versions.Version;

public class VersionTracker {
    
    enum VersionState {
        
        CURRENT,

        IN_PROGRESS,

        SKIPPED,

        POSTPONED,

        FAILED
        
    }
    
    private Version version;
    
    private VersionState versionState = VersionState.CURRENT;
    
}
