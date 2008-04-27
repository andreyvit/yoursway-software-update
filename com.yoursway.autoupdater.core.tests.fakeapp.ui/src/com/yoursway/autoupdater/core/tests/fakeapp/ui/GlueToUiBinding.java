package com.yoursway.autoupdater.core.tests.fakeapp.ui;

import org.eclipse.swt.widgets.Display;

import com.yoursway.autoupdate.core.ProposedUpdate;
import com.yoursway.autoupdate.core.glue.GlueIntegrator;
import com.yoursway.autoupdate.core.glue.GlueIntegratorListener;
import com.yoursway.autoupdate.core.glue.state.schedule.Schedule;
import com.yoursway.autoupdate.ui.UpdatePreferencesCallback;
import com.yoursway.autoupdate.ui.UpdatePreferencesComposite;

public class GlueToUiBinding implements GlueIntegratorListener, UpdatePreferencesCallback {
    
    private final GlueIntegrator integrator;
    private UpdatePreferencesComposite updatePreferences;

    public GlueToUiBinding(GlueIntegrator integrator) {
        this.integrator = integrator;
        integrator.addListener(this);
    }
    
    public synchronized void hook(UpdatePreferencesComposite composite) {
        if (updatePreferences == composite)
            return;
        updatePreferences = composite;
        updatePreferences.setCallback(this);
        updateWorkIndicator();
    }
    
    public synchronized void unhook(UpdatePreferencesComposite composite) {
        if (updatePreferences == composite)
            updatePreferences = null;
    }

    public void askUserDecision(ProposedUpdate undecidedUpdate) {
//        new UpdateInformationDialog(Display.getDefault().getActiveShell(),
//                Activator);
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
                    else
                        composite.reportNoUpdatesFound();
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
