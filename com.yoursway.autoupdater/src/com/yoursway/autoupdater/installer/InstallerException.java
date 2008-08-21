package com.yoursway.autoupdater.installer;


public class InstallerException extends Exception {
    private static final long serialVersionUID = -7255863763631906155L;
    
    public InstallerException(String message) {
        super(message);
    }
    
    public InstallerException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
