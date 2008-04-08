package com.yoursway.autoupdate.core.versions.definitions;

public enum UpdaterAffiliation {
    
    NONE {
        
        public boolean isUpdater() {
            return false;
        }

    },
    
    UPDATER {
        
        public boolean isUpdater() {
            return true;
        }
        
    },
    
    UPDATER_MAIN {
        
        public boolean isUpdater() {
            return true;
        }
        
    };
    
    public abstract boolean isUpdater(); 
    
}
