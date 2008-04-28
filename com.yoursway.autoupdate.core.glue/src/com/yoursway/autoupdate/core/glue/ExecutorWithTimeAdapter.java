package com.yoursway.autoupdate.core.glue;

import java.util.concurrent.Executor;

import com.yoursway.autoupdate.core.glue.ext.Clock;

public class ExecutorWithTimeAdapter implements ExecutorWithTime {
    
    private final Executor executor;
    private final Clock clock;

    public ExecutorWithTimeAdapter(Executor executor, Clock clock) {
        if (executor == null)
            throw new NullPointerException("executor is null");
        if (clock == null)
            throw new NullPointerException("clock is null");
        this.executor = executor;
        this.clock = clock;
    }

    public void execute(final Runnable longPart, final RunnableWithTime finisher) {
        executor.execute(new Runnable() {

            public void run() {
                longPart.run();
                finisher.run(clock.now());
            }
            
        });
    }
    
}
