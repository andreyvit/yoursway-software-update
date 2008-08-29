package com.yoursway.autoupdater.auxiliary;


public class SuiteLoadingException extends AutoupdaterException {
    private static final long serialVersionUID = 6443336370941918502L;
    
    public SuiteLoadingException(Throwable e) {
        super("Cannot load suite", e);
    }
    
}
