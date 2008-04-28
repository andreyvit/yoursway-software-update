package com.yoursway.autoupdate.core;

public interface ProposedUpdate {
  
    VersionDescription targetVersion();
    
    String changesDescription();
    
}
