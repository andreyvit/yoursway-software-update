package com.yoursway.autoupdate.core.glue.checkres;

public abstract class CheckResultImpl implements CheckResult {
    
    @Override
    public abstract String toString();
    
    public boolean isNoWriteAccessResult() {
        return false;
    }
    
}
