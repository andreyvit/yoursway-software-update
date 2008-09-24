package com.yoursway.autoupdater.core.filelibrary.downloader;

import java.net.URL;

public interface DownloaderListener {
    
    void someBytesDownloaded(URL url);
    
    void completed(URL url);
    
    void cancelled(URL url);
    
}
