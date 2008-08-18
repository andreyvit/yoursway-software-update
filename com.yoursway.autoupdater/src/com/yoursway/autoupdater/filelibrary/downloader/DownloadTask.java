package com.yoursway.autoupdater.filelibrary.downloader;

import java.io.File;
import java.net.URL;

class DownloadTask {
    
    final URL url;
    final File file;
    public long loaded;
    
    DownloadTask(URL url, File file, long loaded) {
        this.url = url;
        this.file = file;
        this.loaded = loaded;
    }
    
}
