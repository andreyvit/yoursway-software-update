package com.yoursway.autoupdater.tests;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static com.yoursway.utils.YsFileUtils.createTempFolder;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.junit.Test;

import com.yoursway.autoupdater.auxiliary.ComponentStopper;
import com.yoursway.autoupdater.auxiliary.Product;
import com.yoursway.autoupdater.auxiliary.ProductVersion;
import com.yoursway.autoupdater.filelibrary.Request;
import com.yoursway.autoupdater.installer.Installer;
import com.yoursway.autoupdater.installer.InstallerException;
import com.yoursway.autoupdater.installer.InstallerImpl;

public class InstallerTests {
    
    private static final String TEMP_FOLDER_PREFIX = "autoupdater.installer.test";
    
    @Test
    public void install() throws IOException, InstallerException {
        
        Installer installer = new InstallerImpl();
        
        Product product = new Product();
        Collection<Request> p = newLinkedList();
        
        ProductVersion current = new ProductVersion(product, p);
        ProductVersion version = new ProductVersion(product, p);
        Map<String, File> packs = newHashMap();
        File target = createTempFolder(TEMP_FOLDER_PREFIX, null);
        File extInstallerFolder = createTempFolder(TEMP_FOLDER_PREFIX, null);
        ComponentStopper stopper = new ComponentStopper() {
            public boolean stop() {
                return true;
            }
        };
        
        installer.install(current, version, packs, target, extInstallerFolder, stopper);
        
    }
    
}
