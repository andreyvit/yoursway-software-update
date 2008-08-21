package com.yoursway.autoupdater.installer;

import java.io.File;
import java.util.Map;

import com.yoursway.autoupdater.auxiliary.ComponentStopper;
import com.yoursway.autoupdater.auxiliary.ProductVersion;

public class InstallerImpl implements Installer {
    
    public void install(ProductVersion current, ProductVersion version, Map<String, File> packs, File target,
            File extInstallerFolder, ComponentStopper stopper) throws InstallerException {
        
        if (!current.product().equals(version.product()))
            throw new AssertionError("Tried to update one product to another.");
        
        ExternalInstaller installer = new ExternalInstaller(extInstallerFolder);
        installer.prepare(current, version, packs, target);
        
        installer.start();
        
        boolean stopped = stopper.stop();
        if (!stopped)
            throw new InstallerException("Cannot stop the application");
        
    }
}
