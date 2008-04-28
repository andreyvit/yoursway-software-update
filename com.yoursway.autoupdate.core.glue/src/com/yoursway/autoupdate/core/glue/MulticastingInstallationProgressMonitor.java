package com.yoursway.autoupdate.core.glue;

import static com.yoursway.utils.Listeners.newListenersByIdentity;

import com.yoursway.autoupdate.core.InstallationProgressMonitor;
import com.yoursway.utils.Listeners;

public class MulticastingInstallationProgressMonitor implements InstallationProgressMonitor {
    
    private transient Listeners<InstallationProgressMonitor> monitors = newListenersByIdentity();
    
    public synchronized void addMonitor(InstallationProgressMonitor monitor) {
        monitors.add(monitor);
    }
    
    public synchronized void removeMonitor(InstallationProgressMonitor monitor) {
        monitors.remove(monitor);
    }
    
    public void downloading(long totalBytes) {
        for (InstallationProgressMonitor monitor : monitors)
            monitor.downloading(totalBytes);
    }
    
    public void downloadingProgress(long bytesDone) {
        for (InstallationProgressMonitor monitor : monitors)
            monitor.downloadingProgress(bytesDone);
    }
    
    public void finished() {
        for (InstallationProgressMonitor monitor : monitors)
            monitor.finished();
    }
    
    public void finishing() {
        for (InstallationProgressMonitor monitor : monitors)
            monitor.finishing();
    }
    
    public void installing() {
        for (InstallationProgressMonitor monitor : monitors)
            monitor.installing();
    }
    
    public void starting() {
        for (InstallationProgressMonitor monitor : monitors)
            monitor.starting();
    }
    
}
