package com.yoursway.autoupdater.core.installer;

import java.io.File;
import java.util.Map;

import com.yoursway.autoupdater.core.auxiliary.ProductVersionDefinition;
import com.yoursway.autoupdater.core.installer.Installation;

public interface InstallationCreator {
    
    Installation createInstallation(ProductVersionDefinition currentVD, ProductVersionDefinition newVD,
            Map<String, File> packs, File target, String executablePath);
    
}
