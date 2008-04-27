package com.yoursway.autoupdate.core;

import com.yoursway.autoupdate.core.versions.Version;

public interface ProposedUpdate {
  
    Version targetVersion();
    
    String targetVersionDisplayName();
    
    String changesDescription();
    
}
