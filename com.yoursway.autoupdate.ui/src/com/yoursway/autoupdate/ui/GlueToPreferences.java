package com.yoursway.autoupdate.ui;

import java.util.Date;

import org.eclipse.swt.widgets.Display;

import com.yoursway.autoupdate.core.InstallationProgressMonitor;
import com.yoursway.autoupdate.core.ProposedUpdate;
import com.yoursway.autoupdate.core.glue.GlueIntegrator;
import com.yoursway.autoupdate.core.glue.GlueIntegratorListener;
import com.yoursway.autoupdate.core.glue.state.overall.Attempt;
import com.yoursway.autoupdate.core.glue.state.schedule.Schedule;

public class GlueToPreferences implements GlueIntegratorListener, UpdatePreferencesCallback,
        InstallationProgressMonitor {
    
    private final GlueIntegrator integrator;
    private UpdatePreferencesComposite updatePreferences;
    private final Display display;
    private long totalBytes;
    
    public GlueToPreferences(GlueIntegrator integrator, Display display) {
        if (integrator == null)
            throw new NullPointerException("integrator is null");
        if (display == null)
            throw new NullPointerException("display is null");
        this.integrator = integrator;
        this.display = display;
        integrator.addListener(this);
        integrator.addInstallationProgressMonitor(this);
    }
    
    public synchronized void hook(UpdatePreferencesComposite composite) {
        if (updatePreferences == composite)
            return;
        updatePreferences = composite;
        updatePreferences.setCallback(this);
        updatePreferences.setSchedule(integrator.getSchedule());
        updateWorkIndicator();
    }
    
    public synchronized void unhook(UpdatePreferencesComposite composite) {
        if (updatePreferences == composite)
            updatePreferences = null;
    }
    
    public void askUserDecision(ProposedUpdate undecidedUpdate) {
    }
    
    public void startedOrStoppedCheckingForUpdates() {
        updateWorkIndicator();
    }
    
    public void startedOrStoppedInstalling() {
        if (integrator.isInstallingUpdates())
            showPreferencesComposite();
        else
            updateWorkIndicator();
    }
    
    protected void showPreferencesComposite() {
    }
    
    private synchronized void updateWorkIndicator() {
        if (updatePreferences != null) {
            if (integrator.isInstallingUpdates())
                return;
            final boolean isChecking = integrator.isCheckingForUpdates();
            display.asyncExec(new Runnable() {
                
                public void run() {
                    if (isChecking)
                        updatePreferences.reportChecking();
                    else {
                        Attempt attempt = integrator.getLastCheckAttemp();
                        if (!attempt.exists())
                            updatePreferences.reportNeverChecked();
                        else if (attempt.hasFailed())
                            updatePreferences.reportLastFailedCheck(new Date(attempt.time()));
                        else
                            updatePreferences.reportLastCheck(new Date(attempt.time()));
                    }
                }
                
            });
        }
    }
    
    public void checkNow() {
        integrator.checkForUpdates();
    }
    
    public void setSchedule(Schedule schedule) {
        integrator.setSchedule(schedule);
    }
    
    public synchronized void starting() {
        display.asyncExec(new Runnable() {
            
            public void run() {
                synchronized (GlueToPreferences.this) {
                    if (updatePreferences != null)
                        updatePreferences.reportStartingInstallation();
                }
            }
            
        });
    }
    
    public synchronized void downloading(final long totalBytes) {
        this.totalBytes = totalBytes;
        display.asyncExec(new Runnable() {
            
            public void run() {
                synchronized (GlueToPreferences.this) {
                    if (updatePreferences != null)
                        updatePreferences.reportDownloadingUpdates(0, totalBytes);
                }
            }
            
        });
    }
    
    public synchronized void downloadingProgress(final long bytesDone) {
        display.asyncExec(new Runnable() {
            
            public void run() {
                synchronized (GlueToPreferences.this) {
                    if (updatePreferences != null)
                        updatePreferences.reportDownloadingUpdates(bytesDone, totalBytes);
                }
            }
            
        });
    }
    
    public synchronized void installing() {
        display.asyncExec(new Runnable() {
            
            public void run() {
                synchronized (GlueToPreferences.this) {
                    if (updatePreferences != null)
                        updatePreferences.reportInstallingUpdates();
                }
            }
            
        });
    }
    
    public synchronized void finishing() {
        display.asyncExec(new Runnable() {
            
            public void run() {
                synchronized (GlueToPreferences.this) {
                    if (updatePreferences != null)
                        updatePreferences.reportFinishingInstallation();
                }
            }
            
        });
    }
    
    public synchronized void finished() {
    }
    
}
