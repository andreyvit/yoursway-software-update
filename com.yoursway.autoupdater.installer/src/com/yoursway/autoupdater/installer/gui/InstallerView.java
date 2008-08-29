package com.yoursway.autoupdater.installer.gui;

import com.yoursway.autoupdater.installer.log.InstallerLog;

public interface InstallerView {
    
    InstallerLog getLog();
    
    void doMessageLoop();
    
    void done();
    
}
