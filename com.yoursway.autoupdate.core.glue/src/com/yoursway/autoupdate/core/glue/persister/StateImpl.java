package com.yoursway.autoupdate.core.glue.persister;

import static com.yoursway.utils.Listeners.newListenersByIdentity;

import com.yoursway.autoupdate.core.glue.state.overall.OverallState;
import com.yoursway.autoupdate.core.glue.state.overall.OverallStateImpl;
import com.yoursway.autoupdate.core.glue.state.overall.OverallStateListener;
import com.yoursway.autoupdate.core.glue.state.schedule.ScheduleState;
import com.yoursway.autoupdate.core.glue.state.schedule.ScheduleStateImpl;
import com.yoursway.autoupdate.core.glue.state.schedule.ScheduleStateListener;
import com.yoursway.autoupdate.core.glue.state.version.VersionStateImpl;
import com.yoursway.autoupdate.core.glue.state.version.VersionStateListener;
import com.yoursway.utils.Listeners;

public class StateImpl implements PersistentState {
    
    private static final long serialVersionUID = 1L;

    private OverallStateImpl overallState;
    
    private ScheduleStateImpl scheduleState;
    
    private VersionStateImpl versionState;
    
    private transient Listeners<StateListener> listeners = newListenersByIdentity();
    
    public StateImpl() {
        overallState = new OverallStateImpl();
        scheduleState = new ScheduleStateImpl();
        versionState = new VersionStateImpl();
        addListenersToChildren();
    }
    
    public StateImpl(StateMemento memento) {
        overallState = new OverallStateImpl(memento.overallStateMemento);
        scheduleState = new ScheduleStateImpl(memento.scheduleStateMemento);
        versionState = new VersionStateImpl(memento.versionStateMemento);
        addListenersToChildren();
    }
    
    public synchronized void addListener(StateListener listener) {
        listeners.add(listener);
    }
    
    public synchronized void removeListener(StateListener listener) {
        listeners.remove(listener);
    }

    public OverallState overallState() {
        return overallState;
    }

    public ScheduleState scheduleState() {
        return scheduleState;
    }
    
    public VersionStateImpl versionState() {
        return versionState;
    }
    
    private void addListenersToChildren() {
        overallState.addListener(new OverallStateListener() {

            public void overallStateChanged(long now) {
                fireStateChanged();
            }
            
        });
        scheduleState.addListener(new ScheduleStateListener() {

            public void scheduleChanged(long now) {
                fireStateChanged();
            }
            
        });
        versionState.addListener(new VersionStateListener() {

            public void versionStateChanged(long now) {
                fireStateChanged();
            }
            
        });
    }
    
    void fireStateChanged() {
        for(StateListener listener : listeners)
            listener.stateChanged();
    }
    
    public StateMemento createMemento() {
        return new StateMemento(overallState.createMemento(), scheduleState.createMemento(),
                versionState.createMemento());
    }
    
}
