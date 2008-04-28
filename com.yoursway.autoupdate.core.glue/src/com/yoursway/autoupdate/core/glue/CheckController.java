package com.yoursway.autoupdate.core.glue;

import com.yoursway.autoupdate.core.CheckEngine;
import com.yoursway.autoupdate.core.ProposedUpdate;
import com.yoursway.autoupdate.core.checkres.CheckResult;
import com.yoursway.autoupdate.core.glue.state.overall.OverallState;
import com.yoursway.autoupdate.core.glue.state.overall.OverallStateListener;
import com.yoursway.autoupdate.core.glue.state.version.VersionStateImpl;

public class CheckController implements OverallStateListener {
    
    private final OverallState overallState;
    
    private final VersionStateImpl versionState;
    
    private boolean isChecking = false;
    
    private final CheckEngine checkEngine;
    
    private final ExecutorWithTime executor;
    
    public CheckController(CheckEngine checkEngine, ExecutorWithTime executor, 
            OverallState overallState, VersionStateImpl versionState) {
        if (checkEngine == null)
            throw new NullPointerException("checkEngine is null");
        if (executor == null)
            throw new NullPointerException("executor is null");
        if (overallState == null)
            throw new NullPointerException("overallState is null");
        if (versionState == null)
            throw new NullPointerException("versionState is null");
        this.overallState = overallState;
        this.versionState = versionState;
        this.checkEngine = checkEngine;
        this.executor = executor;
        
        overallState.addListener(this);
    }
    
    public synchronized void overallStateChanged(long now) {
        if (overallState.state().isExpectingUpdateCheckResult() && !isChecking)
            startCheckingForUpdates();
    }
    
    private void startCheckingForUpdates() {
        isChecking = true;
        final CheckResult[] result = new CheckResult[1];
        executor.execute(new Runnable() {
            
            public void run() {
                result[0] = checkEngine.checkForUpdates();
            }
            
        }, new RunnableWithTime() {

            public void run(long now) {
                processResult(result[0], now);
            }
            
        });
    }
    
    protected synchronized void processResult(CheckResult result, long now) {
        isChecking = false;
        overallState.finishedCheckingForUpdates(now, result);
        ProposedUpdate update = result.foundUpdateOrNull();
        if (update != null)
            versionState.freshVersionFound(now, update);
    }
}
