package com.yoursway.autoupdater.auxiliary;

public class SuiteDefinitionLoadingException extends AutoupdaterException {
    private static final long serialVersionUID = 6443336370941918502L;
    
    public SuiteDefinitionLoadingException(Throwable e) {
        super("Cannot load suite definition", e);
    }
    
}
