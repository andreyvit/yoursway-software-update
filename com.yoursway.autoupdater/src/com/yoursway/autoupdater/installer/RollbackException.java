package com.yoursway.autoupdater.installer;

public class RollbackException extends InstallerException {
    private static final long serialVersionUID = 7197946869939830845L;
    
    public RollbackException() {
        super("Cannot rollback installation");
    }
    
}
