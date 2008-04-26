package com.yoursway.autoupdate.core.glue.state.overall;

import static com.yoursway.autoupdate.core.glue.ext.Clocks.isConcrete;

import com.yoursway.autoupdate.core.glue.ext.Clocks;

public class Attempt {

    private final long time;
    private final boolean failed;

    public Attempt(long time, boolean failed) {
        this.time = time;
        this.failed = failed;
    }
    
    public boolean exists() {
        return isConcrete(time);
    }
    
    public long time() {
        return time;
    }
    
    public boolean hasFailed() {
        return failed;
    }

    public boolean isAfter(long otherTime) {
        if (!exists())
            return false;
        return Clocks.isAfter(time, otherTime);
    }
    
    @Override
    public String toString() {
        return Clocks.toString(time) + (failed ? "<F>" : "");
    }
    
}
