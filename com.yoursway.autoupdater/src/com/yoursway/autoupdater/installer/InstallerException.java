package com.yoursway.autoupdater.installer;

import com.yoursway.autoupdater.auxiliary.AutoupdaterException;

public class InstallerException extends AutoupdaterException {
    private static final long serialVersionUID = -7255863763631906155L;
    
    public InstallerException(String message) {
        super(message);
    }
    
    public InstallerException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
