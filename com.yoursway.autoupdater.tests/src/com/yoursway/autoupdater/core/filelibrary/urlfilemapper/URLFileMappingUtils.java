package com.yoursway.autoupdater.core.filelibrary.urlfilemapper;

import java.io.File;
import java.net.URL;

import com.yoursway.autoupdater.core.filelibrary.urlfilemapper.URLFileMapping;

public class URLFileMappingUtils {
    
    public static URLFileMapping createMapping(URL url, File file) {
        return new URLFileMapping(url, file);
    }
}
