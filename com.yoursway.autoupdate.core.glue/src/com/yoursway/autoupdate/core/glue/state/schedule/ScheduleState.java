package com.yoursway.autoupdate.core.glue.state.schedule;

public interface ScheduleState {
    
    void addListener(ScheduleStateListener listener);
    
    void removeListener(ScheduleStateListener listener);
    
    Schedule getSchedule();
    
    void setSchedule(Schedule schedule, long now);

    long lastScheduleChangeTime();
    
}