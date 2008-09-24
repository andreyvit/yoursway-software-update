package com.yoursway.autoupdater.core.localrepository.internal;

import com.yoursway.autoupdater.core.auxiliary.AutoupdaterException;
import com.yoursway.autoupdater.core.installer.InstallerException;

public class InstallingFailedException extends AutoupdaterException {
    private static final long serialVersionUID = 1189834226317848402L;
    
    public InstallingFailedException(InstallerException e) {
        super(e);
    }
    
}
