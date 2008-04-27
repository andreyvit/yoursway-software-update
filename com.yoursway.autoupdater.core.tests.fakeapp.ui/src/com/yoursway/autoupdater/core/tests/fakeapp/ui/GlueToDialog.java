package com.yoursway.autoupdater.core.tests.fakeapp.ui;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.Display;

import com.yoursway.autoupdate.core.ProposedUpdate;
import com.yoursway.autoupdate.core.glue.GlueIntegrator;
import com.yoursway.autoupdate.core.glue.GlueIntegratorListener;
import com.yoursway.autoupdate.ui.DialogUtils;
import com.yoursway.autoupdate.ui.UpdateInformationDialog;

public class GlueToDialog implements GlueIntegratorListener {
    
    private final GlueIntegrator integrator;
    private DialogFactory dialogFactory;
    
    public GlueToDialog(GlueIntegrator integrator, IDialogSettings settings) {
        this.integrator = integrator;
        integrator.addListener(this);
        dialogFactory = new DialogFactory(settings);
    }
    
    public void askUserDecision(final ProposedUpdate undecidedUpdate) {
        Runnable runnable = new Runnable() {

            public void run() {
                UpdateInformationDialog dialog = dialogFactory.createDialog();
                dialog.create();
                dialog.setContentsFrom(undecidedUpdate);
                dialog.setBlockOnOpen(false);
                dialog.open();
            }
            
        };
        Display.getDefault().asyncExec(runnable);
    }
    
    static class DialogFactory {
        
        private IDialogSettings settings;
        
        public DialogFactory(IDialogSettings settings) {
            settings = DialogUtils.lookup(settings, UpdateInformationDialog.class.getSimpleName());
        }
        
        public UpdateInformationDialog createDialog() {
            return new UpdateInformationDialog(Display.getDefault().getActiveShell(), settings);
        }
        
    }

    public void startedOrStoppedCheckingForUpdates() {
    }
    
}
