package com.yoursway.autoupdater.internal;

import com.yoursway.utils.EventSource;

public interface DownloadProgress {
    
    boolean successful();
    
    EventSource<DownloadProgressListener> events();
    
    int progressPercents();
    
}
