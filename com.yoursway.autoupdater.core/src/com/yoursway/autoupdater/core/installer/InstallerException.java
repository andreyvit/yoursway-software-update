package com.yoursway.autoupdater.core.installer;

import com.yoursway.autoupdater.core.auxiliary.AutoupdaterException;

public class InstallerException extends AutoupdaterException {
    private static final long serialVersionUID = -7255863763631906155L;
    
    public InstallerException(String message) {
        super(message);
    }
    
    public InstallerException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
