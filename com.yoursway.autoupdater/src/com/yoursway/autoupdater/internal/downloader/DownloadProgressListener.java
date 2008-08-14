package com.yoursway.autoupdater.internal.downloader;

public interface DownloadProgressListener {
    
    void progressChanged();
    
    void completed();
    
}
