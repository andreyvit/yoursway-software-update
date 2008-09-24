package com.yoursway.autoupdater.core.filelibrary.downloader;

import java.net.URL;

import com.yoursway.autoupdater.core.filelibrary.urlfilemapper.URLFileMapping;
import com.yoursway.utils.EventSource;

public interface Downloader {
    
    EventSource<DownloaderListener> events();
    
    boolean enqueue(URLFileMapping mapping, long loaded);
    
    boolean cancel(URL url);
    
    boolean loading(URL url);
    
}
