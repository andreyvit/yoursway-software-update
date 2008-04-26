package com.yoursway.autoupdate.core.glue.persister;

import java.io.Serializable;

import com.yoursway.autoupdate.core.glue.state.schedule.ScheduleStateMemento;

public class StateMemento implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    final Serializable overallStateMemento;
    final ScheduleStateMemento scheduleStateMemento;

    public StateMemento(Serializable overallStateMemento, ScheduleStateMemento scheduleStateMemento) {
        this.overallStateMemento = overallStateMemento;
        this.scheduleStateMemento = scheduleStateMemento;
    }
    
}
