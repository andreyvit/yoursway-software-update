package com.yoursway.autoupdate.core.checkres;

import com.yoursway.autoupdate.core.ProposedUpdate;

public interface CheckResult {
    
    boolean isSuccess();
    
    boolean isNoWriteAccessResult();
    
    void accept(CheckResultVisitor visitor);

    boolean updatesFound();
    
    ProposedUpdate foundUpdateOrNull();
    
}
