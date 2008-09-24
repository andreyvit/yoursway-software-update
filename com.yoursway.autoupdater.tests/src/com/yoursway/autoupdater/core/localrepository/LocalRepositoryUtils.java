package com.yoursway.autoupdater.core.localrepository;

import java.io.File;
import java.io.IOException;

import com.yoursway.autoupdater.core.auxiliary.UpdatableApplicationProductFeaturesProvider;
import com.yoursway.autoupdater.core.installer.Installer;
import com.yoursway.autoupdater.core.localrepository.LocalRepository;

public class LocalRepositoryUtils {
    
    public static LocalRepository createLocalRepository(
            UpdatableApplicationProductFeaturesProvider featuresProvider, Installer installer, File placeDir)
            throws IOException {
        
        return new LocalRepository(featuresProvider, installer, placeDir);
    }
    
}
