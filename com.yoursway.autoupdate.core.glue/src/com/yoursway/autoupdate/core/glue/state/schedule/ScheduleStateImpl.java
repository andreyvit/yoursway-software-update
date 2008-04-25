package com.yoursway.autoupdate.core.glue.state.schedule;

import static com.yoursway.utils.Listeners.newListenersByIdentity;

import com.yoursway.utils.Listeners;

public class ScheduleStateImpl implements ScheduleState {
    
    private long lastScheduleChangeTime = -1;
    
    private Schedule schedule = Schedule.DAILY;

    private transient Listeners<ScheduleStateListener> listeners = newListenersByIdentity();
    
    public synchronized void addListener(ScheduleStateListener listener) {
        listeners.add(listener);
    }
    
    public synchronized void removeListener(ScheduleStateListener listener) {
        listeners.remove(listener);
    }
    
    
    public synchronized Schedule getSchedule() {
        return schedule;
    }
    
    public synchronized void setSchedule(Schedule schedule, long now) {
        this.lastScheduleChangeTime = now;
        this.schedule = schedule;
        for (ScheduleStateListener listener : listeners)
            listener.scheduleChanged(now);
    }
    
    public synchronized long lastScheduleChangeTime() {
        return lastScheduleChangeTime;
    }
    
}
