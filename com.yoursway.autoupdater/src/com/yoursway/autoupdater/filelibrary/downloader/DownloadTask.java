package com.yoursway.autoupdater.filelibrary.downloader;

import java.io.File;
import java.net.URL;

class DownloadTask {
    
    final URL url;
    final File file;
    
    DownloadTask(URL url, File file) {
        this.url = url;
        this.file = file;
    }
    
}
