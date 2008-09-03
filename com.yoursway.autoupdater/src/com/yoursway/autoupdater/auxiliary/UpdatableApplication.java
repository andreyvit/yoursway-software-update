package com.yoursway.autoupdater.auxiliary;

import java.io.File;
import java.io.IOException;

import com.yoursway.autoupdater.localrepository.LocalRepository;

public interface UpdatableApplication {
    
    File rootFolder(String productName) throws IOException;
    
    SuiteDefinition suite();
    
    LocalRepository localRepository();
    
    ComponentStopper componentStopper(String productName);
    
}
