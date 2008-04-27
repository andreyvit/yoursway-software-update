package com.yoursway.autoupdater.core.tests.fakeapp.ui;

import org.eclipse.swt.widgets.Display;

import com.yoursway.autoupdate.core.glue.sheduling.RelativeScheduler;

public class SwtRelativeScheduler implements RelativeScheduler {
    
    private final Display display;

    public SwtRelativeScheduler(Display display) {
        if (display == null)
            throw new NullPointerException("display is null");
        this.display = display;
    }

    public void schedule(final Runnable runnable, final int delayInMilliseconds) {
        if (delayInMilliseconds <= 0)
            display.asyncExec(runnable);
        else
            display.asyncExec(new Runnable() {

                public void run() {
                    display.timerExec(delayInMilliseconds, runnable);
                }
                
            });
    }
    
}
