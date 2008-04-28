package com.yoursway.autoupdate.core.glue;

import static com.yoursway.utils.Listeners.newListenersByIdentity;

import java.util.concurrent.Executor;

import com.yoursway.autoupdate.core.CheckEngine;
import com.yoursway.autoupdate.core.ProposedUpdate;
import com.yoursway.autoupdate.core.glue.ext.Clock;
import com.yoursway.autoupdate.core.glue.persister.PersistentState;
import com.yoursway.autoupdate.core.glue.persister.Persister;
import com.yoursway.autoupdate.core.glue.persister.PersisterNonOperational;
import com.yoursway.autoupdate.core.glue.persister.StateFactory;
import com.yoursway.autoupdate.core.glue.persister.StateImpl;
import com.yoursway.autoupdate.core.glue.persister.StateMemento;
import com.yoursway.autoupdate.core.glue.persister.Storage;
import com.yoursway.autoupdate.core.glue.sheduling.RelativeScheduler;
import com.yoursway.autoupdate.core.glue.sheduling.Scheduler;
import com.yoursway.autoupdate.core.glue.state.overall.Attempt;
import com.yoursway.autoupdate.core.glue.state.overall.Mode;
import com.yoursway.autoupdate.core.glue.state.overall.OverallStateListener;
import com.yoursway.autoupdate.core.glue.state.schedule.Schedule;
import com.yoursway.autoupdate.core.glue.state.version.VersionStateListener;
import com.yoursway.utils.Listeners;

public class GlueIntegratorImpl implements GlueIntegrator, OverallStateListener, VersionStateListener {
    
    private Persister persister;
    private StateImpl state;
    private Scheduler scheduler;
    private UpdateTimingConfigurationImpl timing;
    private CheckController check;
    private final Clock clock;
    
    private transient Listeners<GlueIntegratorListener> listeners = newListenersByIdentity();
    
    public GlueIntegratorImpl(Clock clock, CheckEngine checkEngine, Executor backgroundExecutor,
            RelativeScheduler relativeScheduler, Storage storage) {
        this.clock = clock;
        try {
            persister = new Persister(storage, relativeScheduler, new StateFactory() {
                
                public PersistentState createEmptyState() {
                    return new StateImpl();
                }
                
                public PersistentState createState(Object memento) {
                    return new StateImpl((StateMemento) memento);
                }
                
            });
            state = (StateImpl) persister.state();
            
            if (state.overallState().state() == Mode.UPDATING)
                doCleanupAfterUpdating();
            
            timing = new UpdateTimingConfigurationImpl(state.overallState(), state.scheduleState());
            scheduler = new RelativeToAbsoluteScheduler(relativeScheduler, clock);
            AutomaticUpdatesScheduler automatic = new AutomaticUpdatesScheduler(scheduler, timing, state.overallState());
            check = new CheckController(checkEngine, backgroundExecutor, clock, state.overallState(), state
                    .versionState());
            
            state.overallState().addListener(this);
            state.versionState().addListener(this);
            
            long startUpTime = clock.now();
            state.overallState().startup(startUpTime);
            automatic.applicationStarted(startUpTime);
        } catch (PersisterNonOperational e) {
            throw new AssertionError(e);
        }
    }
    
    private void doCleanupAfterUpdating() {
        // TODO Auto-generated method stub
    }

    public synchronized void addListener(GlueIntegratorListener listener) {
        listeners.add(listener);
    }
    
    public synchronized void removeListener(GlueIntegratorListener listener) {
        listeners.remove(listener);
    }

    public Schedule getSchedule() {
        return state.scheduleState().getSchedule();
    }
    
    public void setSchedule(Schedule schedule) {
        state.scheduleState().setSchedule(schedule, clock.now());
    }
    
    public void checkForUpdates() {
        state.overallState().startCheckingForUpdatesManually(clock.now());
    }
    
    public boolean isCheckingForUpdates() {
        return state.overallState().state().isExpectingUpdateCheckResult();
    }

    public void overallStateChanged(long now) {
        for(GlueIntegratorListener listener : listeners)
            listener.startedOrStoppedCheckingForUpdates();
    }

    public void versionStateChanged(long now) {
        ProposedUpdate undecidedUpdate = state.versionState().getUndecidedUpdateIfExists();
        if (undecidedUpdate != null)
            for(GlueIntegratorListener listener : listeners)
                listener.askUserDecision(undecidedUpdate);
        else {
            for(GlueIntegratorListener listener : listeners)
                listener.startedOrStoppedInstalling();
            
        }
    }

    public Attempt getLastCheckAttemp() {
        return state.overallState().lastCheckAttempt();
    }

    public void installUpdate(ProposedUpdate update) {
        long now = clock.now();
        if (state.versionState().install(update, now))
            state.overallState().startInstallation(now);
    }

    public void postponeUpdate(ProposedUpdate update) {
        state.versionState().postpone(update, clock.now());
    }

    public void skipUpdate(ProposedUpdate update) {
        state.versionState().skip(update, clock.now());
    }

    public boolean isInstallingUpdates() {
        return state.overallState().state() == Mode.UPDATING;
    }
    
    public int updatesInstallationProgress() {
        return 15;
    }
    
}
