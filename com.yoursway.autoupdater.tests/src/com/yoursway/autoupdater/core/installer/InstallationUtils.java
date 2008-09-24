package com.yoursway.autoupdater.core.installer;

import java.io.File;
import java.util.Map;

import com.yoursway.autoupdater.core.installer.Installation;

public class InstallationUtils {
    
    public static Map<String, File> packs(Installation installation) {
        return installation.packs;
    }
    
}
