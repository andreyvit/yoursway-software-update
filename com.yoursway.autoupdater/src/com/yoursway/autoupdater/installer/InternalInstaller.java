package com.yoursway.autoupdater.installer;

import java.io.IOException;

import com.yoursway.autoupdater.auxiliary.ComponentStopper;
import com.yoursway.autoupdater.installer.log.ConsoleLog;
import com.yoursway.autoupdater.installer.log.InstallerLog;

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
        } catch (IOException e) {
            throw new InstallerException("Cannot perform the installation", e);
        }
    }
    
}
