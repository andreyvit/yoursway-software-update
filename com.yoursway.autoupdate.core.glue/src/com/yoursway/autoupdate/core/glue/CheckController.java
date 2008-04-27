package com.yoursway.autoupdate.core.glue;

import java.util.concurrent.Executor;

import com.yoursway.autoupdate.core.CheckEngine;
import com.yoursway.autoupdate.core.ProposedUpdate;
import com.yoursway.autoupdate.core.checkres.CheckResult;
import com.yoursway.autoupdate.core.glue.ext.Clock;
import com.yoursway.autoupdate.core.glue.state.overall.OverallState;
import com.yoursway.autoupdate.core.glue.state.overall.OverallStateListener;
import com.yoursway.autoupdate.core.glue.state.version.VersionStateImpl;

public class CheckController implements OverallStateListener {
    
    private final OverallState overallState;
    
    private final VersionStateImpl versionState;
    
    private boolean isChecking = false;
    
    private final CheckEngine checkEngine;
    
    private final Clock clock;
    
    private final Executor executor;
    
    public CheckController(CheckEngine checkEngine, Executor executor, Clock clock,
            OverallState overallState, VersionStateImpl versionState) {
        if (checkEngine == null)
            throw new NullPointerException("checkEngine is null");
        if (executor == null)
            throw new NullPointerException("executor is null");
        if (clock == null)
            throw new NullPointerException("clock is null");
        if (overallState == null)
            throw new NullPointerException("overallState is null");
        if (versionState == null)
            throw new NullPointerException("versionState is null");
        this.overallState = overallState;
        this.versionState = versionState;
        this.checkEngine = checkEngine;
        this.clock = clock;
        this.executor = executor;
        
        overallState.addListener(this);
    }
    
    public synchronized void overallStateChanged(long now) {
        if (overallState.state().isExpectingUpdateCheckResult() && !isChecking)
            startCheckingForUpdates();
    }
    
    private void startCheckingForUpdates() {
        isChecking = true;
        executor.execute(new Runnable() {
            
            public void run() {
                processResult(checkEngine.checkForUpdates());
            }
            
        });
    }
    
    protected synchronized void processResult(CheckResult result) {
        long now = clock.now();
        isChecking = false;
        overallState.finishedCheckingForUpdates(now, result);
        ProposedUpdate update = result.foundUpdateOrNull();
        if (update != null)
            versionState.freshVersionFound(now, update);
    }
}
