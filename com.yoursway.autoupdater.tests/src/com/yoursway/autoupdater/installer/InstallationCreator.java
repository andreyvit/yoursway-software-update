package com.yoursway.autoupdater.installer;

import java.io.File;
import java.util.Map;

import com.yoursway.autoupdater.auxiliary.ProductVersionDefinition;

public interface InstallationCreator {
    
    Installation createInstallation(ProductVersionDefinition currentVD, ProductVersionDefinition newVD,
            Map<String, File> packs, File target, String executablePath);
    
}
