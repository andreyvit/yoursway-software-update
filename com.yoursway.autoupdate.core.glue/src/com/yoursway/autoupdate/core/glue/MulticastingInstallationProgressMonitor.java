package com.yoursway.autoupdate.core.glue;

import static com.yoursway.utils.Listeners.newListenersByIdentity;

import com.yoursway.autoupdate.core.InstallationProgressMonitor;
import com.yoursway.utils.Listeners;

public class MulticastingInstallationProgressMonitor implements InstallationProgressMonitor {
    
    private transient Listeners<InstallationProgressMonitor> monitors = newListenersByIdentity();
    
    private enum Event {
        
        NONE {
            
            public void reportTo(InstallationProgressMonitor monitor, long totalBytes, long bytesDone) {
            }
            
        },
        
        STARTING {
            
            public void reportTo(InstallationProgressMonitor monitor, long totalBytes, long bytesDone) {
                monitor.starting();
            }
            
        },
        
        DOWNLOADING {
            
            public void reportTo(InstallationProgressMonitor monitor, long totalBytes, long bytesDone) {
                monitor.downloading(totalBytes);
                monitor.downloadingProgress(bytesDone);
            }
            
        },
        
        INSTALLING {
            
            public void reportTo(InstallationProgressMonitor monitor, long totalBytes, long bytesDone) {
                monitor.installing();
            }
            
        },
        
        FINISHING {
            
            public void reportTo(InstallationProgressMonitor monitor, long totalBytes, long bytesDone) {
                monitor.finishing();
            }
            
        }

        ;
        
        public abstract void reportTo(InstallationProgressMonitor monitor, long totalBytes, long bytesDone);
        
    }
    
    private long totalBytes, bytesDone;
    
    private Event lastEvent = Event.NONE;
    
    public synchronized void addMonitor(InstallationProgressMonitor monitor) {
        monitors.add(monitor);
        lastEvent.reportTo(monitor, totalBytes, bytesDone);
    }
    
    public synchronized void removeMonitor(InstallationProgressMonitor monitor) {
        monitors.remove(monitor);
    }
    
    public synchronized void downloading(long totalBytes) {
        lastEvent = Event.DOWNLOADING;
        this.totalBytes = totalBytes;
        this.bytesDone = 0;
        for (InstallationProgressMonitor monitor : monitors)
            monitor.downloading(totalBytes);
    }
    
    public synchronized void downloadingProgress(long bytesDone) {
        this.bytesDone = bytesDone;
        for (InstallationProgressMonitor monitor : monitors)
            monitor.downloadingProgress(bytesDone);
    }
    
    public synchronized void finished() {
        lastEvent = Event.NONE;
        for (InstallationProgressMonitor monitor : monitors)
            monitor.finished();
    }
    
    public synchronized void finishing() {
        lastEvent = Event.FINISHING;
        for (InstallationProgressMonitor monitor : monitors)
            monitor.finishing();
    }
    
    public synchronized void installing() {
        lastEvent = Event.INSTALLING;
        for (InstallationProgressMonitor monitor : monitors)
            monitor.installing();
    }
    
    public synchronized void starting() {
        lastEvent = Event.STARTING;
        for (InstallationProgressMonitor monitor : monitors)
            monitor.starting();
    }
    
}
