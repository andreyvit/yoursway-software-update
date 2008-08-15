package com.yoursway.autoupdater.filelibrary.downloader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread.State;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.yoursway.utils.EventSource;
import com.yoursway.utils.broadcaster.Broadcaster;
import com.yoursway.utils.broadcaster.BroadcasterFactory;

public class Downloader {
    
    private final DownloadThread thread;
    private final Queue<DownloadTask> tasks = new ConcurrentLinkedQueue<DownloadTask>();
    
    private final Broadcaster<DownloaderListener> broadcaster = BroadcasterFactory
            .newBroadcaster(DownloaderListener.class);
    
    public Downloader() {
        thread = new DownloadThread();
    }
    
    public EventSource<DownloaderListener> events() {
        return broadcaster;
    }
    
    public void enqueue(URL url, File file) {
        synchronized (this) {
            tasks.add(new DownloadTask(url, file));
            notify();
        }
        
        State s = thread.getState();
        if (s == State.TERMINATED)
            throw new IllegalStateException("Downloading thread has been terminated.");
        if (s == State.NEW)
            thread.start();
    }
    
    private class DownloadThread extends Thread {
        public DownloadThread() {
            super(Downloader.this.toString());
        }
        
        @Override
        public void run() {
            try {
                DownloadTask task;
                while (true) {
                    synchronized (Downloader.this) {
                        task = tasks.poll();
                        if (task == null) {
                            Downloader.this.wait();
                            continue;
                        }
                    }
                    
                    download(task);
                }
            } catch (InterruptedException e) {
                interrupted();
            }
        }
        
        private void download(DownloadTask task) {
            InputStream in = null;
            OutputStream out = null;
            
            try {
                in = task.url.openStream();
                out = new BufferedOutputStream(new FileOutputStream(task.file));
                
                byte[] buffer = new byte[1024];
                int read;
                while (true) {
                    read = in.read(buffer);
                    if (read == -1)
                        return;
                    
                    out.write(buffer, 0, read);
                    
                    broadcaster.fire().someBytesDownloaded(task.url);
                }
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
                
                broadcaster.fire().completed(task.url);
            }
            
        }
    }
}
