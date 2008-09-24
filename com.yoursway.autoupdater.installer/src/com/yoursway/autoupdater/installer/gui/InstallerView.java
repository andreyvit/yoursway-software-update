package com.yoursway.autoupdater.installer.gui;

import com.yoursway.autoupdater.core.installer.log.InstallerLog;

public interface InstallerView {
    
    InstallerLog getLog();
    
    void doMessageLoop();
    
    void done();
    
}
