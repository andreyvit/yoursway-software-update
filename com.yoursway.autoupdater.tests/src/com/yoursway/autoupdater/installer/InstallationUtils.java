package com.yoursway.autoupdater.installer;

import java.io.File;
import java.util.Map;

public class InstallationUtils {
    
    public static Map<String, File> packs(Installation installation) {
        return installation.packs;
    }
    
}
