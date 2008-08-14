package com.yoursway.autoupdater.internal;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.yoursway.autoupdater.auxiliary.DownloadTask;
import com.yoursway.autoupdater.auxiliary.DownloadTaskItem;
import com.yoursway.utils.EventSource;
import com.yoursway.utils.broadcaster.Broadcaster;
import com.yoursway.utils.broadcaster.BroadcasterFactory;

public class DownloadThread extends Thread {
    
    private final DownloadTask task;
    private final String place;
    
    private final Broadcaster<DownloadProgressListener> progressBroadcaster = BroadcasterFactory
            .newBroadcaster(DownloadProgressListener.class);
    
    private int loadedBytes = 0;
    private final int totalBytes;
    
    public DownloadThread(DownloadTask task, String place) {
        this.task = task;
        this.place = place;
        
        totalBytes = task.totalBytes();
    }
    
    @Override
    public void run() {
        for (DownloadTaskItem item : task.items()) {
            //> check if the file has been downloaded already 
            download(item);
        }
        
        progressBroadcaster.fire().completed();
    }
    
    private void download(DownloadTaskItem item) {
        InputStream in = null;
        OutputStream out = null;
        
        try {
            in = new URL(item.url()).openStream();
            out = new BufferedOutputStream(new FileOutputStream(place + item.filename()));
            
            byte[] buffer = new byte[1024];
            int read;
            while (true) {
                read = in.read(buffer);
                if (read == -1)
                    return;
                
                out.write(buffer, 0, read);
                
                loadedBytes += read;
                progressBroadcaster.fire().progressChanged();
            }
            
            //> check file size
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
    }
    
    public DownloadProgress progress() {
        return new DownloadProgress() {
            
            public EventSource<DownloadProgressListener> events() {
                return progressBroadcaster;
            }
            
            public int progressPercents() {
                return (int) (((long) loadedBytes) * 100 / totalBytes);
            }
            
            public boolean successful() {
                throw new UnsupportedOperationException();
            }
            
        };
    }
}
