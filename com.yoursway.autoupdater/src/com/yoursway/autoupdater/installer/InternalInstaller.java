package com.yoursway.autoupdater.installer;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.yoursway.autoupdater.auxiliary.ComponentStopper;
import com.yoursway.autoupdater.auxiliary.ProductVersion;
import com.yoursway.autoupdater.installer.gui.ConsoleView;
import com.yoursway.autoupdater.installer.gui.InstallerView;

public class InternalInstaller implements Installer {
    
    private final InstallerView view;
    
    public InternalInstaller() {
        this(new ConsoleView());
    }
    
    public InternalInstaller(InstallerView view) {
        this.view = view;
    }
    
    public void install(ProductVersion current, ProductVersion version, Map<String, File> packs, File target,
            File extInstallerFolder, ComponentStopper stopper) throws InstallerException {
        
        Installation installation = new Installation(current, version, packs, target, view);
        try {
            installation.perform();
        } catch (IOException e) {
            throw new InstallerException("Cannot perform the installation", e);
        }
    }
    
}
