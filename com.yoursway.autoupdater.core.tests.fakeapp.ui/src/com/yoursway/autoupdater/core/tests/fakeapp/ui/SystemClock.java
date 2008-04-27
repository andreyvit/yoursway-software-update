package com.yoursway.autoupdater.core.tests.fakeapp.ui;

import com.yoursway.autoupdate.core.glue.ext.Clock;

public class SystemClock implements Clock {

    public long now() {
        return System.currentTimeMillis();
    }
    
}
