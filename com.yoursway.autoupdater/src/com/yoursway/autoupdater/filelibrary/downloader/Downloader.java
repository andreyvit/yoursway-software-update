package com.yoursway.autoupdater.filelibrary.downloader;

import java.io.File;
import java.net.URL;

import com.yoursway.utils.EventSource;

public interface Downloader {
    
    EventSource<DownloaderListener> events();
    
    boolean enqueue(URL url, File file, long loaded);
    
    boolean cancel(URL url);
    
    boolean loading(URL url, File file);
    
}
