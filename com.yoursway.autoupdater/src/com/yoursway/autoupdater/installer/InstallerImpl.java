package com.yoursway.autoupdater.installer;

import java.io.File;
import java.util.Map;

import com.yoursway.autoupdater.auxiliary.ComponentStopper;
import com.yoursway.autoupdater.auxiliary.ProductVersion;
import com.yoursway.autoupdater.installer.external.ExternalInstaller;
import com.yoursway.autoupdater.installer.external.InstallerClient;

public class InstallerImpl implements Installer {
    
    public void install(ProductVersion current, ProductVersion version, Map<String, File> packs, File target,
            File extInstallerFolder, ComponentStopper stopper) throws InstallerException {
        
        if (!current.product().equals(version.product()))
            throw new AssertionError("Tried to update one product to another.");
        
        ExternalInstaller installer = new ExternalInstaller(extInstallerFolder);
        installer.prepare(current, version, packs, target);
        
        installer.start();
        
        try {
            ExternalInstaller.client().receive(InstallerClient.READY);
            ExternalInstaller.client().send(InstallerClient.STOPPING);
        } catch (Exception e) {
            throw new InstallerException("Cannot communicate with the external installer", e);
        }
        
        boolean stopped = stopper.stop();
        if (!stopped)
            throw new InstallerException("Cannot stop the application");
        
    }
}
