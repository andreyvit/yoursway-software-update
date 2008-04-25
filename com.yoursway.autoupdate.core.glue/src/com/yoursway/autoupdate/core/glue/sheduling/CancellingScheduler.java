package com.yoursway.autoupdate.core.glue.sheduling;

import com.yoursway.autoupdate.core.glue.RunnableWithTime;

public class CancellingScheduler implements Scheduler {
    
    private final Scheduler downstream;
    
    private volatile int currentSequenceId;

    public CancellingScheduler(Scheduler downstream) {
        if (downstream == null)
            throw new NullPointerException("downstream is null");
        this.downstream = downstream;
    }

    public synchronized void schedule(final RunnableWithTime runnable, long at) {
        final int id = ++currentSequenceId;
        downstream.schedule(new RunnableWithTime() {

            public void run(long now) {
                if (id != currentSequenceId)
                    return;
                runnable.run(now);
            }
            
        }, at);
    }
    
    
    
}
