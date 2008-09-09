package com.yoursway.autoupdater.auxiliary;

public class SuiteDefinitionLoadingException extends AutoupdaterException {
    private static final long serialVersionUID = 6443336370941918502L;
    
    public SuiteDefinitionLoadingException(String updateSite, String name, Throwable e) {
        super(message(updateSite, name, e), e);
    }
    
    private static String message(String updateSite, String name, Throwable e) {
        return String.format("Cannot load %s suite definition from %s\n%s: %s", name, updateSite, e
                .getClass().getSimpleName(), e.getMessage());
    }
}
