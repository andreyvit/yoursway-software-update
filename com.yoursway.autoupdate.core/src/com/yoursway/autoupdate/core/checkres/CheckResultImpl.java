package com.yoursway.autoupdate.core.checkres;

import com.yoursway.autoupdate.core.ProposedUpdate;

public abstract class CheckResultImpl implements CheckResult {
    
    @Override
    public abstract String toString();
    
    public boolean isNoWriteAccessResult() {
        return false;
    }
    
    public boolean updatesFound() {
        return false;
    }
    
    public ProposedUpdate foundUpdateOrNull() {
        return null;
    }
    
}
