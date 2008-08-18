package com.yoursway.autoupdater.filelibrary;

import static com.google.common.collect.Maps.newHashMap;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import com.yoursway.autoupdater.filelibrary.downloader.AbstractDownloader;
import com.yoursway.utils.YsFileUtils;

public class DownloaderMock extends AbstractDownloader {
    
    private final Map<URL, File> files = newHashMap();
    
    public void enqueue(URL url, File file, long loaded) {
        files.put(url, file);
    }
    
    public void createFile(Request request) throws IOException {
        File file = files.get(request.url);
        StringBuilder b = new StringBuilder();
        b.setLength((int) request.size);
        YsFileUtils.writeString(file, b.toString());
        
        broadcaster.fire().someBytesDownloaded(request.url);
        broadcaster.fire().completed(request.url);
    }
    
    @Override
    public boolean loading(URL url, File file) {
        return false;
    }
}
