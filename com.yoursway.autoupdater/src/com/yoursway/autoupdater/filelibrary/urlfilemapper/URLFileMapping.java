package com.yoursway.autoupdater.filelibrary.urlfilemapper;

import java.io.File;
import java.net.URL;

public class URLFileMapping {
    
    private final URL url;
    private final File file;
    
    URLFileMapping(URL url, File file) {
        this.url = url;
        this.file = file;
    }
    
    public URL url() {
        return url;
    }
    
    public File file() {
        return file;
    }
    
}
