/**
 * 
 */
package com.yoursway.autoupdate.core.glue.state.overall;

public enum Mode {
    
    DISABLED {
        
        public boolean canStartAutomaticCheckingForUpdates() {
            return false;
        }
        
        public boolean canStartManualCheckingForUpdates() {
            return true;
        }
        
    },
    
    NO_UPDATES {
        
        public boolean canStartAutomaticCheckingForUpdates() {
            return true;
        }
        
    },
    
    MANUAL_CHECK {
        
        public boolean canStartAutomaticCheckingForUpdates() {
            return false;
        }
        
        public boolean shouldGiveWarningAboutDisabledWriteAccess() {
            return true;
        }
        
        public boolean isExpectingUpdateCheckResult() {
            return true;
        }

    },
    
    AUTOMATIC_CHECK {
        
        public boolean canStartAutomaticCheckingForUpdates() {
            return false;
        }
        
        public boolean isExpectingUpdateCheckResult() {
            return true;
        }

    },
    
    UPDATE_FOUND_ACTIONS_UNDECIDED {
        
        public boolean canStartAutomaticCheckingForUpdates() {
            return true;
        }
        
    },
    
    UPDATING {
        
        public boolean canStartAutomaticCheckingForUpdates() {
            return false;
        }
        
    };
    
    public abstract boolean canStartAutomaticCheckingForUpdates();
    
    public boolean canStartManualCheckingForUpdates() {
        return canStartAutomaticCheckingForUpdates();
    }
    
    public final boolean canStartCheckingForUpdates(boolean isManual) {
        return isManual ? canStartManualCheckingForUpdates() : canStartAutomaticCheckingForUpdates();
    }

    public boolean shouldGiveWarningAboutDisabledWriteAccess() {
        return false;
    }
    
    public boolean isExpectingUpdateCheckResult() {
        return false;
    }
   
}