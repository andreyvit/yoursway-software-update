package com.yoursway.autoupdate.core.glue;

public interface ExecutorWithTime {
    
    void execute(Runnable longPart, RunnableWithTime finisher);
    
}
