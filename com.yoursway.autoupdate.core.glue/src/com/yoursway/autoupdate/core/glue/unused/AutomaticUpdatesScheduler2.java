//package com.yoursway.autoupdate.core.glue.unused;
//
//import com.yoursway.autoupdate.core.glue.UpdateTimingConfiguration;
//import com.yoursway.autoupdate.core.glue.checkres.CheckResult;
//import com.yoursway.autoupdate.core.glue.checkres.InternalFailureCheckResult;
//import com.yoursway.autoupdate.core.glue.ext.Clock;
//import com.yoursway.autoupdate.core.glue.ext.Clocks;
//import com.yoursway.autoupdate.core.glue.sheduling.CancellingScheduler;
//import com.yoursway.autoupdate.core.glue.sheduling.Scheduler;
//import com.yoursway.autoupdate.core.glue.state.overall.OverallState;
//import com.yoursway.autoupdate.core.glue.state.overall.OverallStateImpl;
//import com.yoursway.autoupdate.core.glue.state.schedule.Schedule;
//import com.yoursway.autoupdate.core.glue.state.schedule.ScheduleState;
//import com.yoursway.autoupdate.core.glue.state.schedule.ScheduleStateImpl;
//
//public class AutomaticUpdatesScheduler2 {
//    
//    private final UpdateTimingConfiguration timing;
//    
//    private final Clock clock;
//    
//    private final Updater updater;
//    
//    public AutomaticUpdatesScheduler2(Scheduler scheduler, Updater updater, Clock clock,
//            UpdateTimingConfiguration timingConfiguration) {
//        this.updater = updater;
//        this.clock = clock;
//        if (timingConfiguration == null)
//            throw new NullPointerException("timingConfiguration is null");
//        this.timing = timingConfiguration;
//        this.scheduler = new CancellingScheduler(scheduler);
//    }
//    
//    private final Runnable updateRunnable = new Runnable() {
//        
//        public void run() {
//            handleScheduledCheckCheck();
//        }
//        
//    };
//    
//    private OverallState overallState = new OverallStateImpl();
//    
//    private ScheduleState scheduleState = new ScheduleStateImpl();
//    
//    private CancellingScheduler scheduler;
//    
//    public void applicationStarted() {
//        overallState.startup(clock.now());
//        scheduleUpdateCheckCheck(true);
//    }
//    
//    public void handleScheduledCheckCheck() {
//        doExecuteUpdate(false);
//    }
//    
//    private void doExecuteUpdate(boolean isManual) {
//        long now = clock.now();
//        boolean ok = isManual ? overallState.startCheckingForUpdatesManually(now)
//                : overallState.startCheckingForUpdatesAutomatically(now);
//        if (!ok)
//            return;
//        new UpdateChecker().start();
//    }
//    
//    public void checkForUpdates() {
//        doExecuteUpdate(true);
//    }
//    
//    public void setSchedule(Schedule schedule) {
//        scheduleState.setSchedule(schedule, clock.now());
//        scheduleUpdateCheckCheck(false);
//    }
//    
//    private void scheduleUpdateCheckCheck(boolean isStartup) {
//        long nextCheck = nextUpdateTime(isStartup);
//        if (nextCheck >= 0)
//            scheduler.schedule(updateRunnable, nextCheck);
//    }
//    
//    private long nextUpdateTime(boolean isStartup) {
//        Schedule schedule = scheduleState.getSchedule();
//        if (schedule == Schedule.MANUAL)
//            return Clock.NEVER;
//        else {
//            long now = clock.now();
//            long time = now;
//            time = Clocks.max(time, overallState.calculateAutomaticUpdateLimit(schedule, timing, isStartup));
//            time = Clocks.max(time, scheduleState.calculateAutomaticUpdateLimit(timing));
//            if (time < now)
//                throw new AssertionError("time < now in nextUpdateTime()");
//            return time;
//        }
//        
//    }
//  
//    void handleCheckResult(CheckResult result) {
//        overallState.finishedCheckingForUpdates(result);
//    }
//    
//    private class UpdateChecker extends Thread {
//        
//        @Override
//        public void run() {
//            CheckResult result;
//            try {
//                result = updater.checkForUpdates();
//            } catch (Throwable e) {
//                result = new InternalFailureCheckResult(e);
//            }
//            handleCheckResult(result);
//        }
//        
//    }
//    
//}
