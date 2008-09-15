package com.yoursway.autoupdater.installer;

import java.io.File;
import java.util.Map;

import com.yoursway.autoupdater.auxiliary.ProductVersionDefinition;

public class InstallationUtils {
    
    public static Installation createInstallation(ProductVersionDefinition currentVD,
            ProductVersionDefinition newVD, Map<String, File> packs, File target, String executablePath) {
        
        return new Installation(currentVD, newVD, packs, target, executablePath);
    }
    
    public static Map<String, File> packs(Installation installation) {
        return installation.packs;
    }
    
}
