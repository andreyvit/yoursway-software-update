package com.yoursway.autoupdater.internal.downloader;

import com.yoursway.utils.EventSource;

public interface DownloadProgress {
    
    boolean successful();
    
    EventSource<DownloadProgressListener> events();
    
    int progressPercents();
    
}
