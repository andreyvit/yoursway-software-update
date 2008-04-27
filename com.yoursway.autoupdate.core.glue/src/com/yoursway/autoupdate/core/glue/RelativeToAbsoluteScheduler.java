package com.yoursway.autoupdate.core.glue;

import static com.yoursway.autoupdate.core.glue.ext.Clocks.sub;

import com.yoursway.autoupdate.core.glue.ext.Clock;
import com.yoursway.autoupdate.core.glue.sheduling.RelativeScheduler;
import com.yoursway.autoupdate.core.glue.sheduling.Scheduler;

public class RelativeToAbsoluteScheduler implements Scheduler {
    
    private final RelativeScheduler relativeScheduler;
    private final Clock clock;

    public RelativeToAbsoluteScheduler(RelativeScheduler relativeScheduler, Clock clock) {
        if (relativeScheduler == null)
            throw new NullPointerException("relativeScheduler is null");
        if (clock == null)
            throw new NullPointerException("clock is null");
        this.relativeScheduler = relativeScheduler;
        this.clock = clock;
    }

    public void schedule(final RunnableWithTime runnable, long at) {
        long delay = sub(at, clock.now());
        if (delay < 0)
            delay = 0;
        relativeScheduler.schedule(new Runnable() {

            public void run() {
                runnable.run(clock.now());
            }
            
        }, (int) delay);
    }
    
}
