package com.yoursway.autoupdater.auxiliary;

import java.util.LinkedList;
import java.util.List;

public class DownloadTask {
    
    private final List<DownloadTaskItem> items = new LinkedList<DownloadTaskItem>();
    
    public Iterable<DownloadTaskItem> items() {
        return items;
    }
    
    public int totalBytes() {
        int bytes = 0;
        for (DownloadTaskItem item : items)
            bytes += item.filesize();
        return bytes;
    }
    
}
