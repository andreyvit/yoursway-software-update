package com.yoursway.autoupdate.core.glue;

public interface UpdateTimingConfiguration {
    
    long nextAutomaticUpdateTime();

    void addListener(UpdateTimingConfigurationListener listener);

    void removeListener(UpdateTimingConfigurationListener listener);
    
}
