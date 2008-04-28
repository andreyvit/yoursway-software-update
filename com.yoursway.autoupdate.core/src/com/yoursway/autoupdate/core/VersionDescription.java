package com.yoursway.autoupdate.core;

import com.yoursway.autoupdate.core.versions.Version;

public interface VersionDescription {
    
    Version version();
    
    String displayName();
    
}
