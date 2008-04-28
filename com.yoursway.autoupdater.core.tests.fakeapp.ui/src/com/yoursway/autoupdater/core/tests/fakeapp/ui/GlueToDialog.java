package com.yoursway.autoupdater.core.tests.fakeapp.ui;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.Display;

import com.yoursway.autoupdate.core.ProposedUpdate;
import com.yoursway.autoupdate.core.glue.GlueIntegrator;
import com.yoursway.autoupdate.core.glue.GlueIntegratorListener;
import com.yoursway.autoupdate.ui.DialogUtils;
import com.yoursway.autoupdate.ui.UpdateInformationDialog;
import com.yoursway.autoupdate.ui.UpdateInformationDialogCallback;

public class GlueToDialog implements GlueIntegratorListener, UpdateInformationDialogCallback {
    
    private final GlueIntegrator integrator;
    private DialogFactory dialogFactory;
    private ProposedUpdate updateProposedInTheDialog;
    private UpdateInformationDialog dialog;
    
    public GlueToDialog(GlueIntegrator integrator, IDialogSettings settings) {
        this.integrator = integrator;
        integrator.addListener(this);
        dialogFactory = new DialogFactory(settings, this);
    }
    
    public synchronized void askUserDecision(final ProposedUpdate undecidedUpdate) {
        this.updateProposedInTheDialog = undecidedUpdate;
        Runnable runnable = new Runnable() {
            
            public void run() {
                createAndShowDialog();
            }
            
        };
        Display.getDefault().asyncExec(runnable);
    }
    
    synchronized void createAndShowDialog() {
        if (dialog == null || dialog.getShell() == null || dialog.getShell().isDisposed()) {
            dialog = dialogFactory.createDialog();
            dialog.create();
            dialog.setBlockOnOpen(false);
        }
        dialog.setContentsFrom(updateProposedInTheDialog);
        dialog.open();
    }
    
    static class DialogFactory {
        
        private IDialogSettings settings;
        private final UpdateInformationDialogCallback callback;
        
        public DialogFactory(IDialogSettings settings, UpdateInformationDialogCallback callback) {
            if (callback == null)
                throw new NullPointerException("callback is null");
            this.callback = callback;
            this.settings = DialogUtils.lookup(settings, UpdateInformationDialog.class.getSimpleName());
        }
        
        public UpdateInformationDialog createDialog() {
            UpdateInformationDialog dialog = new UpdateInformationDialog(Display.getDefault()
                    .getActiveShell(), settings);
            dialog.setCallback(callback);
            return dialog;
        }
        
    }
    
    public synchronized void install() {
        integrator.installUpdate(updateProposedInTheDialog);
    }
    
    public synchronized void postpone() {
        integrator.postponeUpdate(updateProposedInTheDialog);
    }
    
    public synchronized void skip() {
        integrator.skipUpdate(updateProposedInTheDialog);
    }
    
    public void startedOrStoppedCheckingForUpdates() {
    }
    
    public void startedOrStoppedInstalling() {
    }
    
}
