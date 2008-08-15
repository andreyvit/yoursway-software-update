package com.yoursway.autoupdater.filelibrary.downloader;

import java.net.URL;

public interface DownloaderListener {
    
    void someBytesDownloaded(URL url);
    
    void completed(URL url);
    
}
