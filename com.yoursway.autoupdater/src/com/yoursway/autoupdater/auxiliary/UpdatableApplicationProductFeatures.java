package com.yoursway.autoupdater.auxiliary;

import java.io.File;
import java.io.IOException;

import com.yoursway.utils.YsFileUtils;

public interface UpdatableApplicationProductFeatures {
    
    File rootFolder() throws IOException;
    
    ComponentStopper componentStopper();
    
    String executablePath();
    
    String currentVersionDefinitionPath() throws IOException;
    
    UpdatableApplicationProductFeatures MOCK = new UpdatableApplicationProductFeatures() {
        
        private File tmpRoot;
        
        public ComponentStopper componentStopper() {
            return new ComponentStopper() {
                public boolean stop() {
                    System.exit(0);
                    return true;
                }
            };
        }
        
        public File rootFolder() throws IOException {
            if (tmpRoot == null)
                tmpRoot = YsFileUtils.createTempFolder("autoupdater-appRoot-", null);
            return tmpRoot;
        }
        
        public String executablePath() {
            return "Contents/MacOS/eclipse";
        }
        
        public String currentVersionDefinitionPath() throws IOException {
            String path = "version.txt";
            File file = new File(rootFolder(), path);
            YsFileUtils.writeString(file, "PV\tUNNAMED\tcurrent\tcurrent");
            return path;
        }
        
    };
    
}
