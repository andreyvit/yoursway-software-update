package com.yoursway.autoupdate.core.glue.state.schedule;

import static com.yoursway.utils.Listeners.newListenersByIdentity;

import java.io.Serializable;

import com.yoursway.utils.Listeners;

public class ScheduleStateImpl implements ScheduleState {
    
    private long lastScheduleChangeTime;
    
    private Schedule schedule;

    private transient Listeners<ScheduleStateListener> listeners = newListenersByIdentity();
    
    public ScheduleStateImpl() {
        lastScheduleChangeTime = -1;
        schedule = Schedule.DAILY;
    }
    
    public ScheduleStateImpl(ScheduleStateMemento memento) {
        lastScheduleChangeTime = memento.lastChange;
        schedule = memento.schedule;
    }
    
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

    public ScheduleStateMemento createMemento() {
        return new ScheduleStateMemento(schedule, lastScheduleChangeTime);
    }
    
}
