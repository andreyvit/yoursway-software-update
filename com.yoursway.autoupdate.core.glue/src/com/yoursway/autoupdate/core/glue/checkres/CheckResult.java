package com.yoursway.autoupdate.core.glue.checkres;

public interface CheckResult {
    
    boolean isSuccess();
    
    boolean isNoWriteAccessResult();
    
    void accept(CheckResultVisitor visitor);

    boolean updatesFound();
    
}
