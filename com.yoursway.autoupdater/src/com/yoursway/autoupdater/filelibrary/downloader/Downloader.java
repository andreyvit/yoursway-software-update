package com.yoursway.autoupdater.filelibrary.downloader;

import java.io.File;
import java.net.URL;

import com.yoursway.utils.EventSource;

public interface Downloader {
    
    EventSource<DownloaderListener> events();
    
    void enqueue(URL url, File file, long loaded);
    
    void cancel(URL url);
    
    boolean loading(URL url, File file);
    
}
