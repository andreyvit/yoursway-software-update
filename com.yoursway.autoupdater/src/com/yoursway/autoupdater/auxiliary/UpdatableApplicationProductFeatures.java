package com.yoursway.autoupdater.auxiliary;

import java.io.File;
import java.io.IOException;

import com.yoursway.utils.YsFileUtils;

public interface UpdatableApplicationProductFeatures {
    
    File rootFolder() throws IOException;
    
    ComponentStopper componentStopper();
    
    String executablePath();
    
    UpdatableApplicationProductFeatures MOCK = new UpdatableApplicationProductFeatures() {
        
        public ComponentStopper componentStopper() {
            return new ComponentStopper() {
                public boolean stop() {
                    System.exit(0);
                    return true;
                }
            };
        }
        
        public File rootFolder() throws IOException {
            return YsFileUtils.createTempFolder("autoupdater-appRoot-", null);
        }
        
        public String executablePath() {
            return "Contents/MacOS/eclipse";
        }
        
    };
    
}
