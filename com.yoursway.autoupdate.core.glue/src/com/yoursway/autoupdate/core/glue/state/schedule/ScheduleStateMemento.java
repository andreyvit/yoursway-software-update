package com.yoursway.autoupdate.core.glue.state.schedule;

import java.io.Serializable;

public class ScheduleStateMemento implements Serializable {

    private static final long serialVersionUID = 1L;
    
    final Schedule schedule;
    final long lastChange;
    
    public ScheduleStateMemento(Schedule schedule, long lastChange) {
        this.schedule = schedule;
        this.lastChange = lastChange;
    }
    
}
