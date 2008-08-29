package com.yoursway.autoupdater.installer;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.yoursway.autoupdater.auxiliary.ComponentStopper;
import com.yoursway.autoupdater.auxiliary.ProductVersion;
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
    
    public void install(ProductVersion current, ProductVersion version, Map<String, File> packs, File target,
            File extInstallerFolder, ComponentStopper stopper) throws InstallerException {
        
        Installation installation = new Installation(current, version, packs, target, log);
        try {
            installation.perform();
        } catch (IOException e) {
            throw new InstallerException("Cannot perform the installation", e);
        }
    }
    
}
