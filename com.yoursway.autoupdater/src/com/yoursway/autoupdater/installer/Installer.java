package com.yoursway.autoupdater.installer;

import java.io.File;
import java.util.Map;

import com.yoursway.autoupdater.auxiliary.ComponentStopper;
import com.yoursway.autoupdater.auxiliary.ProductVersionDefinition;

public interface Installer {
    
    void install(ProductVersionDefinition current, ProductVersionDefinition version, Map<String, File> packs, File target,
            File extInstallerFolder, ComponentStopper stopper) throws InstallerException;
    
}
