package com.yoursway.autoupdate.ui.eclipse;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class CheckForUpdatesAction implements IWorkbenchWindowActionDelegate {

    private IWorkbenchWindow window;

    public void dispose() {
    }

    public void init(IWorkbenchWindow window) {
        this.window = window;
    }

    public void run(IAction action) {
        RcpAutomaticUpdater updater = AutomaticUpdatesSchedulingStartup.instance();
        updater.checkForUpdates();
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }
    
}
