/**
 * 
 */
package com.yoursway.autoupdate.core.glue.state.version;

public enum VersionDecision {
    
    UNDECIDED {
        
        public boolean canSkipOrPostpone() {
            return true;
        }
        
    },

    INSTALLING {

        public boolean isInstalling() {
            return true;
        }
        
    },

    SKIPPED {
        
        public boolean canSkipOrPostpone() {
            return true;
        }
        
    },

    POSTPONED {
        
        public boolean canSkipOrPostpone() {
            return true;
        }
        
    },

    INSTALLATION_FAILED;
    
    public boolean canSkipOrPostpone() {
        return false;
    }

    public boolean isInstalling() {
        return false;
    }
    
}