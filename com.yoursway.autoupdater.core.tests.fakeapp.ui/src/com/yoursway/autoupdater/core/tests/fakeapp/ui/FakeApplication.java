package com.yoursway.autoupdater.core.tests.fakeapp.ui;

import static com.yoursway.autoupdate.core.glue.state.schedule.Schedule.DAILY;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.yoursway.autoupdate.core.glue.GlueIntegrator;
import com.yoursway.autoupdate.core.glue.GlueIntegratorImpl;
import com.yoursway.autoupdate.core.glue.persister.Storage;
import com.yoursway.autoupdate.core.glue.persister.TransactionalStorage;
import com.yoursway.autoupdate.core.glue.state.schedule.Schedule;
import com.yoursway.autoupdate.ui.DialogUtils;
import com.yoursway.autoupdate.ui.UpdateInformationDialog;
import com.yoursway.autoupdate.ui.UpdatePreferencesCallback;
import com.yoursway.autoupdate.ui.UpdatePreferencesComposite;
import com.yoursway.autoupdater.core.tests.fakeapp.ui.internal.Activator;

public class FakeApplication implements IApplication {
    
    private final static String UPDATE_SCHEDULE_PREF = "update.schedule";
    
    public Object start(IApplicationContext context) throws Exception {
        Display display = new Display();
        
        File stateDir = new File(Activator.getDefault().getStateLocation().toFile(), "updater");
        Storage storage = new TransactionalStorage(new File(stateDir, "state.bin"), new File(stateDir,
                "state.upd"));
        
        Activator.getDefault().getPreferenceStore().setDefault(UPDATE_SCHEDULE_PREF, DAILY.toString());
        String scheduleName = Activator.getDefault().getPreferenceStore().getString(UPDATE_SCHEDULE_PREF);
        Schedule schedule = Schedule.valueOf(scheduleName);
        
        Executor executor = new ThreadPoolExecutor(1, 1, 10000, TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>());
        
        GlueIntegrator glue = new GlueIntegratorImpl(new SystemClock(), new FakeCheckEngine(), executor,
                new SwtRelativeScheduler(display), storage);
        
        GlueToPreferences glueToPreferences = new GlueToPreferences(glue);
        new GlueToDialog(glue, Activator.getDefault().getDialogSettings());
        
        final Shell shell = new Shell(display, SWT.DIALOG_TRIM);
        final UpdatePreferencesComposite prefs = new UpdatePreferencesComposite(shell, SWT.NONE);
        glueToPreferences.hook(prefs);
//        prefs.setCallback(new UpdatePreferencesCallback() {
//            
//            public void checkNow() {
//                prefs.reportChecking();
//                Display.getDefault().timerExec(2000, new Runnable() {
//                    
//                    public void run() {
//                        prefs.reportNoUpdatesFound();
//                        
//                        UpdateInformationDialog dialog = new UpdateInformationDialog(shell, DialogUtils
//                                .lookup(Activator.getDefault().getDialogSettings(), "updateDialog"));
//                        dialog.setBlockOnOpen(false);
//                        dialog.open();
//                    }
//                    
//                });
//                
//            }
//            
//            public void setSchedule(Schedule schedule) {
//                Activator.getDefault().getPreferenceStore().setValue(UPDATE_SCHEDULE_PREF,
//                        schedule.toString());
//                Activator.getDefault().savePluginPreferences();
//            }
//            
//        });
//        
        prefs.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        GridLayoutFactory.swtDefaults().generateLayout(shell);
        
        shell.setSize(500, 300);
        DialogUtils.centerWindow(shell);
        shell.open();
        
        while (!shell.isDisposed())
            if (!display.readAndDispatch())
                display.sleep();
        return IApplication.EXIT_OK;
    }
    
    public void stop() {
    }
    
}
