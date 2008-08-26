package com.yoursway.autoupdater.installer;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.yoursway.autoupdater.auxiliary.ComponentStopper;
import com.yoursway.autoupdater.auxiliary.ProductVersion;

public class InternalInstaller implements Installer {
    
    public void install(ProductVersion current, ProductVersion version, Map<String, File> packs, File target,
            File extInstallerFolder, ComponentStopper stopper) throws InstallerException {
        
        Installation installation = new Installation(current, version, packs, target);
        try {
            installation.perform();
        } catch (IOException e) {
            throw new InstallerException("Cannot perform the installation", e);
        }
    }
    
}
