package com.yoursway.autoupdate.core.glue;

import static com.yoursway.autoupdate.core.glue.ext.Clock.ANYTIME;
import static com.yoursway.autoupdate.core.glue.ext.Clock.MUST_NOT_BE_USED;
import static com.yoursway.autoupdate.core.glue.ext.Clock.NEVER;
import static com.yoursway.autoupdate.core.glue.ext.Clocks.add;
import static com.yoursway.autoupdate.core.glue.ext.Clocks.isConcrete;
import static com.yoursway.autoupdate.core.glue.ext.Clocks.isNotConcrete;
import static com.yoursway.autoupdate.core.glue.state.schedule.Schedule.MANUAL;
import static com.yoursway.utils.Listeners.newListenersByIdentity;

import com.yoursway.autoupdate.core.glue.ext.Clock;
import com.yoursway.autoupdate.core.glue.ext.Clocks;
import com.yoursway.autoupdate.core.glue.state.overall.Attempt;
import com.yoursway.autoupdate.core.glue.state.overall.OverallState;
import com.yoursway.autoupdate.core.glue.state.overall.OverallStateListener;
import com.yoursway.autoupdate.core.glue.state.schedule.Schedule;
import com.yoursway.autoupdate.core.glue.state.schedule.ScheduleState;
import com.yoursway.autoupdate.core.glue.state.schedule.ScheduleStateListener;
import com.yoursway.utils.Listeners;

public class UpdateTimingConfigurationImpl implements UpdateTimingConfiguration, OverallStateListener, ScheduleStateListener {
    
    private static final long SECOND = 1000;
    private static final long MINUTE = SECOND * 60;
    private static final long HOUR = MINUTE * 60;
    private static final long DAY = HOUR * 24;
    private static final long MONTH = DAY * 30;
    private static final long WEEK = DAY * 7;
    
    private long noDailyChecksWarningThreshold = DAY * 7;
    private long noDailyChecksWarningRearm = DAY * 3;
    
    private long noWeeklyChecksWarningThreshold = WEEK * 5;
    private long noWeeklyChecksWarningRearm = WEEK * 2;
    
    private long noChecksCriticalWarningThreshold = 5 * MONTH;
    private long noChecksCriticalWarningRearm = 3 * MONTH;
    
    private long delayAfterSchedule = 5 * SECOND;
    private long delayAfterStartup = 4 * SECOND; //5 * MINUTE;
    private long dailyUpdateInterval = DAY;
    private long weeklyUpdateInterval = WEEK;
    private long warningToUpdateMinimumDelay = 5 * MINUTE;
    
    private long minimumStartupDailyRecheckInterval = 2 * HOUR;
    private long minimumStartupWeeklyRecheckInterval = 2 * DAY;
    private final OverallState overallState;
    private final ScheduleState scheduleState;
    
    private transient Listeners<UpdateTimingConfigurationListener> listeners = newListenersByIdentity();
    
    public UpdateTimingConfigurationImpl(OverallState overallState, ScheduleState scheduleState) {
        this.overallState = overallState;
        this.scheduleState = scheduleState;
        overallState.addListener(this);
        scheduleState.addListener(this);
    }
    
    public synchronized void addListener(UpdateTimingConfigurationListener listener) {
        listeners.add(listener);
    }
    
    public synchronized void removeListener(UpdateTimingConfigurationListener listener) {
        listeners.remove(listener);
    }
    
    public long delayDueToScheduleChange() {
        long lastScheduleChangeTime = scheduleState.lastScheduleChangeTime();
        if (isNotConcrete(lastScheduleChangeTime))
            return ANYTIME;
        return add(lastScheduleChangeTime, delayAfterSchedule);
    }
    
    public long delayDueToLatestCheck(Schedule schedule, Attempt lastAttempt, boolean isStartup) {
        if (!lastAttempt.exists())
            throw new IllegalArgumentException("!lastAttempt.exists()");
        if (schedule == Schedule.DAILY)
            if (lastAttempt.hasFailed() && isStartup)
                return add(lastAttempt.time(), minimumStartupDailyRecheckInterval);
            else
                return add(lastAttempt.time(), dailyUpdateInterval);
        else if (schedule == Schedule.WEEKLY)
            if (lastAttempt.hasFailed() && isStartup)
                return add(lastAttempt.time(), minimumStartupWeeklyRecheckInterval);
            else
                return add(lastAttempt.time(), weeklyUpdateInterval);
        else if (schedule == Schedule.MANUAL)
            throw new IllegalArgumentException("Cannot use delayDueToLatestCheck in manual checking mode");
        else
            throw new AssertionError("Unknown update schedule kind");
    }
    
    public long delayDueToStartup(long startUpTime) {
        if (isNotConcrete(startUpTime))
            throw new IllegalArgumentException("startUpTime not set");
        return add(startUpTime, delayAfterStartup);
    }
    
    public long regularWarningTime(Schedule schedule, long lastSuccessfulCheckTime,
            long firstFailedCheckAfterLastSuccessfulCheckTime, long lastCheckAttempTime) {
        if (isNotConcrete(firstFailedCheckAfterLastSuccessfulCheckTime))
            throw new IllegalArgumentException(
                    "regularWarningTime: firstFailedCheckAfterLastSuccessfulCheckTime is not set)");
        if (schedule == Schedule.DAILY)
            return add(firstFailedCheckAfterLastSuccessfulCheckTime, noDailyChecksWarningThreshold);
        else if (schedule == Schedule.WEEKLY)
            return add(firstFailedCheckAfterLastSuccessfulCheckTime, noWeeklyChecksWarningThreshold);
        else if (schedule == Schedule.MANUAL)
            throw new IllegalArgumentException("Cannot use regularWarningTime in manual checking mode");
        else
            throw new AssertionError("Unknown update schedule kind");
    }
    
    public boolean isUpdateFarEnoughToGiveWarnings(long delay) {
        return delay > warningToUpdateMinimumDelay;
    }
    
    public long criticalWarningTime(Schedule schedule, long lastSuccessfulCheckTime,
            long firstFailedCheckAfterLastSuccessfulCheckTime, long lastCheckAttempTime, long firstRunTime) {
        if (isConcrete(lastSuccessfulCheckTime))
            return add(lastSuccessfulCheckTime, noChecksCriticalWarningThreshold);
        if (isConcrete(firstFailedCheckAfterLastSuccessfulCheckTime))
            return add(firstFailedCheckAfterLastSuccessfulCheckTime, noChecksCriticalWarningThreshold);
        return add(firstRunTime, noChecksCriticalWarningThreshold);
    }
    
    public long delayRepeatedCriticalWarning(long lastWarning) {
        return add(lastWarning, noChecksCriticalWarningRearm);
    }
    
    public long delayRepeatedRegularWarning(Schedule schedule, long lastWarning) {
        if (schedule == Schedule.DAILY)
            return add(lastWarning, noDailyChecksWarningRearm);
        else if (schedule == Schedule.WEEKLY)
            return add(lastWarning, noWeeklyChecksWarningRearm);
        else if (schedule == Schedule.MANUAL)
            throw new IllegalArgumentException(
                    "Cannot use delayRepeatedRegularWarning in manual checking mode");
        else
            throw new AssertionError("Unknown update schedule kind");
    }
    
    public void setNoDailyChecksWarningThreshold(long noDailyChecksWarningThreshold) {
        this.noDailyChecksWarningThreshold = noDailyChecksWarningThreshold;
    }
    
    public void setNoDailyChecksWarningRearm(long noDailyChecksWarningRearm) {
        this.noDailyChecksWarningRearm = noDailyChecksWarningRearm;
    }
    
    public void setNoWeeklyChecksWarningThreshold(long noWeeklyChecksWarningThreshold) {
        this.noWeeklyChecksWarningThreshold = noWeeklyChecksWarningThreshold;
    }
    
    public void setNoWeeklyChecksWarningRearm(long noWeeklyChecksWarningRearm) {
        this.noWeeklyChecksWarningRearm = noWeeklyChecksWarningRearm;
    }
    
    public void setNoChecksCriticalWarningThreshold(long noChecksCriticalWarningThreshold) {
        this.noChecksCriticalWarningThreshold = noChecksCriticalWarningThreshold;
    }
    
    public void setNoChecksCriticalWarningRearm(long noChecksCriticalWarningRearm) {
        this.noChecksCriticalWarningRearm = noChecksCriticalWarningRearm;
    }
    
    public void setDelayAfterSchedule(long delayAfterSchedule) {
        this.delayAfterSchedule = delayAfterSchedule;
    }
    
    public void setDelayAfterStartup(long delayAfterStartup) {
        this.delayAfterStartup = delayAfterStartup;
    }
    
    public void setDailyUpdateInterval(long dailyUpdateInterval) {
        this.dailyUpdateInterval = dailyUpdateInterval;
    }
    
    public void setWeeklyUpdateInterval(long weeklyUpdateInterval) {
        this.weeklyUpdateInterval = weeklyUpdateInterval;
    }
    
    public void setWarningToUpdateMinimumDelay(long warningToUpdateMinimumDelay) {
        this.warningToUpdateMinimumDelay = warningToUpdateMinimumDelay;
    }
    
    public void setMinimumStartupDailyRecheckInterval(long minimumStartupDailyRecheckInterval) {
        this.minimumStartupDailyRecheckInterval = minimumStartupDailyRecheckInterval;
    }
    
    public void setMinimumStartupWeeklyRecheckInterval(long minimumStartupWeeklyRecheckInterval) {
        this.minimumStartupWeeklyRecheckInterval = minimumStartupWeeklyRecheckInterval;
    }
    
    public void resetAllTimesToMustNotBeUsed() {
        noDailyChecksWarningThreshold = MUST_NOT_BE_USED;
        noDailyChecksWarningRearm = MUST_NOT_BE_USED;
        
        noWeeklyChecksWarningThreshold = MUST_NOT_BE_USED;
        noWeeklyChecksWarningRearm = MUST_NOT_BE_USED;
        
        noChecksCriticalWarningThreshold = MUST_NOT_BE_USED;
        noChecksCriticalWarningRearm = MUST_NOT_BE_USED;
        
        delayAfterSchedule = MUST_NOT_BE_USED;
        delayAfterStartup = MUST_NOT_BE_USED;
        dailyUpdateInterval = MUST_NOT_BE_USED;
        weeklyUpdateInterval = MUST_NOT_BE_USED;
        warningToUpdateMinimumDelay = MUST_NOT_BE_USED;
        
        minimumStartupDailyRecheckInterval = MUST_NOT_BE_USED;
        minimumStartupWeeklyRecheckInterval = MUST_NOT_BE_USED;
        
    }
    
    public long calculateAutomaticUpdateLimit() {
        Schedule schedule = scheduleState.getSchedule();
        if (schedule == Schedule.MANUAL)
            return ANYTIME;
        else {
            long limit1 = Clock.ANYTIME;
            Attempt lastAttempt = overallState.lastCheckAttempt();
            if (lastAttempt.exists()) {
                limit1 = delayDueToLatestCheck(schedule, lastAttempt, !lastAttempt.isAfter(overallState
                        .startUpTime()));
            }
            long limit2 = delayDueToStartup(overallState.startUpTime());
            return Clocks.max(limit1, limit2);
        }
    }
    
    public long nextAutomaticUpdateTime() {
        Schedule schedule = scheduleState.getSchedule();
        if (schedule == MANUAL)
            return NEVER;
        else {
            long limit1 = calculateAutomaticUpdateLimit();
            long limit2 = delayDueToScheduleChange();
            return Clocks.max(limit1, limit2);
        }
        
    }
    
    public void overallStateChanged(long now) {
        fireTimeChanged(now);        
    }
    
    public void scheduleChanged(long now) {
        fireTimeChanged(now);        
    }
    
    private void fireTimeChanged(long now) {
        for(UpdateTimingConfigurationListener listener : listeners)
            listener.nextAutomaticUpdateTimeChanged(now);
    }
    
}
