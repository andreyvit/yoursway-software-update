package com.yoursway.autoupdate.ui;

import org.eclipse.swt.widgets.Display;

import com.yoursway.autoupdate.core.glue.state.overall.Mode;

public class DefaultUpdateStateCallback {
    
    private Display display;

    public DefaultUpdateStateCallback() {
        display = Display.getDefault();
    }

    public void schedule(Runnable runnable, int milliseconds) {
        display.timerExec(milliseconds, runnable);
    }

    public void issueCriticalWarning() {
    }

    public void issueNoWriteAccessWarning() {
    }

    public void issueRegularWarning() {
    }

    public void stateChanged(Mode mode) {
    }

    public void schedule(Runnable runnable, long at) {
    }

}
