package com.yoursway.autoupdater.filelibrary.downloader;

import java.io.File;
import java.net.URL;

import com.yoursway.utils.EventSource;

public interface Downloader {
    
    void enqueue(URL url, File file);
    
    EventSource<DownloaderListener> events();
    
}
