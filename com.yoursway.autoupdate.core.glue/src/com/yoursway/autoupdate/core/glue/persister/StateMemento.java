package com.yoursway.autoupdate.core.glue.persister;

import java.io.Serializable;

import com.yoursway.autoupdate.core.glue.state.schedule.ScheduleStateMemento;
import com.yoursway.autoupdate.core.glue.state.version.VersionStateMemento;

public class StateMemento implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    final Serializable overallStateMemento;
    final ScheduleStateMemento scheduleStateMemento;
    final VersionStateMemento versionStateMemento;
    
    public StateMemento(Serializable overallStateMemento, ScheduleStateMemento scheduleStateMemento,
            VersionStateMemento versionStateMemento) {
        this.overallStateMemento = overallStateMemento;
        this.scheduleStateMemento = scheduleStateMemento;
        this.versionStateMemento = versionStateMemento;
    }
    
}
