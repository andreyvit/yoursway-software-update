package com.yoursway.autoupdater.filelibrary.downloader;

import java.io.File;
import java.net.URL;

import com.yoursway.autoupdater.filelibrary.urlfilemapper.URLFileMapping;

class DownloadTask {
    
    final URLFileMapping mapping;
    final long loaded;
    
    DownloadTask(URLFileMapping mapping, long loaded) {
        this.mapping = mapping;
        this.loaded = loaded;
    }
    
    URL url() {
        return mapping.url();
    }
    
    File file() {
        return mapping.file();
    }
    
}
