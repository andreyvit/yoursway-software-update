package com.yoursway.autoupdater.core.tests.fakeapp.ui;

import java.util.Date;

import org.eclipse.swt.widgets.Display;

import com.yoursway.autoupdate.core.ProposedUpdate;
import com.yoursway.autoupdate.core.glue.GlueIntegrator;
import com.yoursway.autoupdate.core.glue.GlueIntegratorListener;
import com.yoursway.autoupdate.core.glue.state.overall.Attempt;
import com.yoursway.autoupdate.core.glue.state.schedule.Schedule;
import com.yoursway.autoupdate.ui.UpdatePreferencesCallback;
import com.yoursway.autoupdate.ui.UpdatePreferencesComposite;

public class GlueToPreferences implements GlueIntegratorListener, UpdatePreferencesCallback {
    
    private final GlueIntegrator integrator;
    private UpdatePreferencesComposite updatePreferences;
    
    public GlueToPreferences(GlueIntegrator integrator) {
        this.integrator = integrator;
        integrator.addListener(this);
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
    
    private synchronized void updateWorkIndicator() {
        if (updatePreferences != null) {
            final boolean isChecking = integrator.isCheckingForUpdates();
            final UpdatePreferencesComposite composite = updatePreferences;
            Runnable runnable = new Runnable() {
                
                public void run() {
                    if (isChecking)
                        composite.reportChecking();
                    else {
                        Attempt attempt = integrator.getLastCheckAttemp();
                        if (!attempt.exists())
                            composite.reportNeverChecked();
                        else if (attempt.hasFailed())
                            composite.reportLastFailedCheck(new Date(attempt.time()));
                        else
                            composite.reportLastCheck(new Date(attempt.time()));
                    }
                }
                
            };
            Display.getDefault().asyncExec(runnable);
        }
    }
    
    public void checkNow() {
        integrator.checkForUpdates();
    }
    
    public void setSchedule(Schedule schedule) {
        integrator.setSchedule(schedule);
    }
    
}
