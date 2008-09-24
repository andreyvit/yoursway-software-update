package com.yoursway.autoupdater.installer.gui;

import com.yoursway.autoupdater.core.installer.log.ConsoleLog;
import com.yoursway.autoupdater.core.installer.log.InstallerLog;

public class ConsoleView implements InstallerView {
    
    public InstallerLog getLog() {
        return ConsoleLog.inst();
    }

    public void doMessageLoop() {
        // nothing
    }
    
    public void done() {
        // nothing
    }
    
}
