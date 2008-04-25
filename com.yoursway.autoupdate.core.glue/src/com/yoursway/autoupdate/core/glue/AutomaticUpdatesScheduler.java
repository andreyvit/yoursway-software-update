package com.yoursway.autoupdate.core.glue;

import static com.yoursway.autoupdate.core.glue.ext.Clocks.isConcrete;

import com.yoursway.autoupdate.core.glue.sheduling.CancellingScheduler;
import com.yoursway.autoupdate.core.glue.sheduling.Scheduler;
import com.yoursway.autoupdate.core.glue.state.overall.OverallState;
import com.yoursway.autoupdate.core.glue.state.overall.OverallStateListener;

public class AutomaticUpdatesScheduler implements OverallStateListener, UpdateTimingConfigurationListener {
    
    private final UpdateTimingConfiguration timing;
    
    private final RunnableWithTime updateRunnable;
    
    private OverallState overallState;
    
    private CancellingScheduler scheduler;
    
    private RunnableWithTime startAutomaticCheck = new RunnableWithTime() {

        public void run(long now) {
            startAutomaticCheck(now);
        }
        
    };
    
    public AutomaticUpdatesScheduler(Scheduler scheduler, RunnableWithTime updateRunnable, 
            UpdateTimingConfiguration timing, OverallState overallState) {
        if (scheduler == null)
            throw new NullPointerException("scheduler is null");
        if (updateRunnable == null)
            throw new NullPointerException("updateRunnable is null");
        if (timing == null)
            throw new NullPointerException("timing is null");
        if (overallState == null)
            throw new NullPointerException("overallState is null");
        this.scheduler = new CancellingScheduler(scheduler);
        this.updateRunnable = updateRunnable;
        this.timing = timing;
        this.overallState = overallState;
        overallState.addListener(this);
        timing.addListener(this);
    }
    
    public void applicationStarted(long now) {
        scheduleAutomaticCheck(now);
    }
    
    private void scheduleAutomaticCheck(long now) {
        long nextCheck = timing.nextAutomaticUpdateTime();
        if (isConcrete(nextCheck))
            scheduler.schedule(startAutomaticCheck, nextCheck);
    }
    
    void startAutomaticCheck(long now) {
        if (!overallState.startCheckingForUpdatesAutomatically(now))
            return;
        updateRunnable.run(now);
    }

    public void overallStateChanged(long now) {
        scheduleAutomaticCheck(now);
    }

    public void nextAutomaticUpdateTimeChanged(long now) {
        scheduleAutomaticCheck(now);
    }
    
}
