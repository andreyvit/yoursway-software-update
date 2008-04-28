package com.yoursway.autoupdater.core.tests.fakeapp.ui;

import com.yoursway.autoupdate.core.InstallationProgressMonitor;
import com.yoursway.autoupdate.core.ProposedUpdate;
import com.yoursway.autoupdate.core.UpdateEngine;

public class FakeUpdateEngine implements UpdateEngine {
    
    public void update(ProposedUpdate update, InstallationProgressMonitor progressMonitor) {
        try {
            progressMonitor.starting();
            Thread.sleep(400);
            int totalBytes = 10 * 1024 * 1024;
            progressMonitor.downloading(totalBytes);
            for (int i = 0; i < 30; i++) {
                Thread.sleep(100);
                progressMonitor.downloadingProgress(i * totalBytes / 30);
            }
            progressMonitor.installing();
            Thread.sleep(1500);
            cleanUpPreviousUpdate(progressMonitor);
        } catch (InterruptedException e) {
        }
    }
    
    public void cleanUpPreviousUpdate(InstallationProgressMonitor progressMonitor) {
        progressMonitor.finishing();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        progressMonitor.finished();
    }

    public boolean checkIfCleanupIsNeeded() {
        return false;
    }
    
}
