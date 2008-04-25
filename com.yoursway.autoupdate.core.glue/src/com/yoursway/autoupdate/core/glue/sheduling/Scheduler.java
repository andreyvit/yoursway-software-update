package com.yoursway.autoupdate.core.glue.sheduling;

import com.yoursway.autoupdate.core.glue.RunnableWithTime;

public interface Scheduler {
    
    void schedule(RunnableWithTime runnable, long at);
    
}
