package com.yoursway.autoupdater.localrepository;

import com.yoursway.autoupdater.auxiliary.AutoupdaterException;

public class LocalRepositoryException extends AutoupdaterException {
    private static final long serialVersionUID = -471580122971541651L;
    
    public LocalRepositoryException(Throwable e) {
        super("Cannot create local repository", e);
    }
    
}
