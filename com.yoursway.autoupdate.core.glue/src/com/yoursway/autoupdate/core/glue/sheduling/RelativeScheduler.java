package com.yoursway.autoupdate.core.glue.sheduling;

public interface RelativeScheduler {
    
    void schedule(Runnable runnable, int delayInMilliseconds);
    
}
