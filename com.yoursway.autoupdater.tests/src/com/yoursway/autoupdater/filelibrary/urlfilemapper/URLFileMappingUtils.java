package com.yoursway.autoupdater.filelibrary.urlfilemapper;

import java.io.File;
import java.net.URL;

public class URLFileMappingUtils {
    
    public static URLFileMapping createMapping(URL url, File file) {
        return new URLFileMapping(url, file);
    }
}
