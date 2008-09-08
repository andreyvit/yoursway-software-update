package com.yoursway.autoupdater.auxiliary;


public interface UpdatableApplication extends UpdatableApplicationProductFeaturesProvider {
    
    String updateSite();
    
    String suiteName();
    
    boolean inInstallingState();
    
    void setInstallingState(boolean value);
    
}
