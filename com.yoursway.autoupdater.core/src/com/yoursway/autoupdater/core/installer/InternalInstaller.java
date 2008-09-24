package com.yoursway.autoupdater.core.installer;

import com.yoursway.autoupdater.core.auxiliary.ComponentStopper;
import com.yoursway.autoupdater.core.installer.log.ConsoleLog;
import com.yoursway.autoupdater.core.installer.log.InstallerLog;

public class InternalInstaller implements Installer {
    
    private final InstallerLog log;
    
    public InternalInstaller() {
        this(ConsoleLog.inst());
    }
    
    public InternalInstaller(InstallerLog log) {
        this.log = log;
    }
    
    public void install(Installation installation, ComponentStopper stopper) throws InstallerException {
        try {
            installation.perform(log);
        } catch (Throwable e) {
            log.error(e);
            installation.rollback();
        }
        
        // only if installation or rollback done successfully
        installation.deleteBackupFiles();
    }
}
