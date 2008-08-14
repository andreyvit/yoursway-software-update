package com.yoursway.autoupdater.internal.downloader;

import java.util.List;

import com.yoursway.autoupdater.auxiliary.DownloadTask;

public class Downloader {
    
    private static Downloader instance;
    private final String place;
    private List<DownloadThread> threads;
    
    private Downloader(String place) {
        this.place = place;
        
        //> add slash after place
        
        //> use a storage provider instead
    }
    
    public DownloadProgress startDownloading(DownloadTask task) {
        DownloadThread thread = new DownloadThread(task, place);
        threads.add(thread);
        return thread.progress();
    }
    
    public static Downloader instance() {
        if (instance == null)
            instance = new Downloader("~/com.yoursway.autoupdater/");
        return instance;
    }
}
