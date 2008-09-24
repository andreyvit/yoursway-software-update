package com.yoursway.autoupdater.core.installer;

import java.io.File;
import java.util.Map;

import com.yoursway.autoupdater.core.auxiliary.ProductVersionDefinition;
import com.yoursway.autoupdater.core.installer.Installation;
import com.yoursway.autoupdater.core.installer.log.InstallerLog;

public class RollbackTestInstallationCreator implements InstallationCreator {
    
    public Installation createInstallation(ProductVersionDefinition currentVD,
            ProductVersionDefinition newVD, Map<String, File> packs, File target, String executablePath) {
        
        return new Installation(currentVD, newVD, packs, target, executablePath) {
            
            private int n = 0;
            
            @Override
            protected void removeOldFile(String path, InstallerLog log) {
                n++;
                if (n == 3)
                    throw new AssertionError("Emulated error");
                
                super.removeOldFile(path, log);
            }
            
        };
        
    }
    
}
