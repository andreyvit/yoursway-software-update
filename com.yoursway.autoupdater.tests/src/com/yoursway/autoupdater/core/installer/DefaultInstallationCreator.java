package com.yoursway.autoupdater.core.installer;

import java.io.File;
import java.util.Map;

import com.yoursway.autoupdater.core.auxiliary.ProductVersionDefinition;
import com.yoursway.autoupdater.core.installer.Installation;

public class DefaultInstallationCreator implements InstallationCreator {
    
    public Installation createInstallation(ProductVersionDefinition currentVD,
            ProductVersionDefinition newVD, Map<String, File> packs, File target, String executablePath) {
        
        return new Installation(currentVD, newVD, packs, target, executablePath);
    }
    
}
