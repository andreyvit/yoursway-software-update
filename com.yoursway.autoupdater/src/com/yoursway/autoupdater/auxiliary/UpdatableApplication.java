package com.yoursway.autoupdater.auxiliary;

import java.io.File;
import java.io.IOException;

public interface UpdatableApplication extends UpdatableApplicationProductFeaturesProvider {
    
    String updateSite();
    
    String suiteName();
    
    UpdatableApplicationView view();
    
    File localRepositoryPlace() throws IOException;
    
}
