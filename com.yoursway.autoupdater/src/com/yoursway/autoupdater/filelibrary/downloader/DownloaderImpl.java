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
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.yoursway.utils.annotations.SynchronizedWithMonitorOfField;
import com.yoursway.utils.annotations.SynchronizedWithMonitorOfThis;

public class DownloaderImpl extends AbstractDownloader {
    
    private final DownloadThread thread;
    
    @SynchronizedWithMonitorOfThis
    private final Queue<DownloadTask> tasks = new ConcurrentLinkedQueue<DownloadTask>();
    
    public DownloaderImpl() {
        thread = new DownloadThread();
    }
    
    public void enqueue(URL url, File file, long loaded) {
        synchronized (this) {
            tasks.add(new DownloadTask(url, file, loaded));
            notify();
        }
        
        State s = thread.getState();
        if (s == State.TERMINATED)
            throw new IllegalStateException("Downloading thread has been terminated.");
        if (s == State.NEW)
            thread.start();
    }
    
    @Override
    public boolean cancel(URL url) {
        synchronized (this) {
            for (Iterator<DownloadTask> it = tasks.iterator(); it.hasNext();) {
                DownloadTask task = it.next();
                
                if (task.url.equals(url)) {
                    it.remove();
                    return true;
                }
            }
            
            if (thread.task.url.equals(url)) {
                thread.cancelCurrentTask();
                return true;
            }
        }
        
        return false;
    }
    
    private class DownloadThread extends Thread {
        
        //?
        @SynchronizedWithMonitorOfField("DownloaderImpl.this")
        DownloadTask task = null;
        
        private volatile boolean cancelled = false;
        
        public DownloadThread() {
            super(DownloaderImpl.this.toString());
        }
        
        @Override
        public void run() {
            try {
                while (true) {
                    synchronized (DownloaderImpl.this) {
                        while (true) {
                            task = tasks.poll();
                            if (task != null)
                                break;
                            
                            DownloaderImpl.this.wait();
                        }
                        cancelled = false;
                    }
                    
                    download(task);
                }
            } catch (InterruptedException e) {
                interrupted();
            }
        }
        
        public void cancelCurrentTask() {
            cancelled = true;
        }
        
        private void download(DownloadTask task) {
            InputStream in = null;
            OutputStream out = null;
            
            try {
                URLConnection connection = task.url.openConnection();
                if (task.loaded > 0)
                    connection.setRequestProperty("Range", "bytes=" + task.loaded + "-");
                
                in = connection.getInputStream();
                out = new BufferedOutputStream(new FileOutputStream(task.file));
                
                byte[] buffer = new byte[1024];
                int read;
                while (!cancelled) {
                    read = in.read(buffer);
                    if (read == -1)
                        break;
                    
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
                
                if (!cancelled)
                    broadcaster.fire().completed(task.url);
            }
            
        }
    }
}
