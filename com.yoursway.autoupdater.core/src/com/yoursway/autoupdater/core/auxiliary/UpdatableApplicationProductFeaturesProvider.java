package com.yoursway.autoupdater.core.auxiliary;

public interface UpdatableApplicationProductFeaturesProvider {
    
    UpdatableApplicationProductFeatures getFeatures(String productName);
    
    UpdatableApplicationProductFeaturesProvider MOCK = new UpdatableApplicationProductFeaturesProvider() {
        
        public UpdatableApplicationProductFeatures getFeatures(String productName) {
            return UpdatableApplicationProductFeatures.MOCK;
        }
        
    };
    
}
