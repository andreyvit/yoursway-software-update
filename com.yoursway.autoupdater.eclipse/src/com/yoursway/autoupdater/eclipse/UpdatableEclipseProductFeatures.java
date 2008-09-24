package com.yoursway.autoupdater.eclipse;

import static com.yoursway.utils.assertions.Assert.assertion;

import java.io.File;
import java.io.IOException;

import com.yoursway.autoupdater.core.auxiliary.ComponentStopper;
import com.yoursway.autoupdater.core.auxiliary.UpdatableApplicationProductFeatures;

public class UpdatableEclipseProductFeatures implements UpdatableApplicationProductFeatures {
    
    public ComponentStopper componentStopper() {
        return new ComponentStopper() {
            public boolean stop() {
                System.exit(0); //!
                
                // haven't been terminated
                return false;
            }
        };
    }
    
    public String currentVersionDefinitionPath() throws IOException {
        return UpdatableEclipse.updatesPath() + "version.txt";
    }
    
    public String executablePath() {
        String absolutePath = System.getProperty("eclipse.launcher");
        try {
            String rootPath = rootFolder().getCanonicalPath();
            assertion(absolutePath.startsWith(rootPath), "Executable should be in app folder");
            
            String relativePath = absolutePath.substring(rootPath.length());
            if (relativePath.startsWith("/") || relativePath.startsWith("\\"))
                relativePath = relativePath.substring(1);
            return relativePath;
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public File rootFolder() throws IOException {
        return UpdatableEclipse.rootFolder();
    }
    
}
