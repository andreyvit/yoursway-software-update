package com.yoursway.autoupdate.ui;

import static com.yoursway.autoupdate.core.glue.state.schedule.Schedule.DAILY;

import java.util.Date;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.yoursway.autoupdate.core.glue.state.schedule.Schedule;
import com.yoursway.autoupdate.ui.internal.Activator;

public class UiTestApplication implements IApplication {
    
    private final static String UPDATE_SCHEDULE_PREF = "update.schedule";
    
    public Object start(IApplicationContext context) throws Exception {
        Activator.getDefault().getPreferenceStore().setDefault(UPDATE_SCHEDULE_PREF, DAILY.toString());
        String scheduleName = Activator.getDefault().getPreferenceStore().getString(UPDATE_SCHEDULE_PREF);
        Schedule schedule = Schedule.valueOf(scheduleName);
        
        Display display = new Display();
        final Shell shell = new Shell(display, SWT.DIALOG_TRIM);
        final UpdatePreferencesComposite prefs = new UpdatePreferencesComposite(shell, SWT.NONE);
        prefs.setSchedule(schedule);
        prefs.setCallback(new UpdatePreferencesCallback() {
            
            public void checkNow() {
                prefs.reportChecking();
                Display.getDefault().timerExec(2000, new Runnable() {
                    
                    public void run() {
                        prefs.reportLastCheck(new Date());
                        
                        UpdateInformationDialog dialog = new UpdateInformationDialog(shell, DialogUtils
                                .lookup(Activator.getDefault().getDialogSettings(), "updateDialog"));
                        dialog.setBlockOnOpen(false);
                        dialog.open();
                    }
                    
                });
            }
            
            public void setSchedule(Schedule schedule) {
                Activator.getDefault().getPreferenceStore().setValue(UPDATE_SCHEDULE_PREF,
                        schedule.toString());
                Activator.getDefault().savePluginPreferences();
            }
            
        });
        
        prefs
                .setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.TOP).grab(true, false)
                        .create());
        GridLayoutFactory.swtDefaults().generateLayout(shell);
        
        shell.setSize(500, 300);
        DialogUtils.centerWindow(shell);
        shell.open();
        
        MessageDialog.openError(shell, "Updates are not working", 
                "You don't have write access to this application, so automatic update cannot proceed.");
        
        while (!shell.isDisposed())
            if (!display.readAndDispatch())
                display.sleep();
        return IApplication.EXIT_OK;
    }
    
    public void stop() {
    }
    
}
