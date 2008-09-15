package com.yoursway.autoupdater.localrepository;

import java.io.File;
import java.io.IOException;

import com.yoursway.autoupdater.auxiliary.UpdatableApplicationProductFeaturesProvider;
import com.yoursway.autoupdater.installer.Installer;

public class LocalRepositoryUtils {
    
    public static LocalRepository createLocalRepository(
            UpdatableApplicationProductFeaturesProvider featuresProvider, Installer installer, File placeDir)
            throws IOException {
        
        return new LocalRepository(featuresProvider, installer, placeDir);
    }
    
}
