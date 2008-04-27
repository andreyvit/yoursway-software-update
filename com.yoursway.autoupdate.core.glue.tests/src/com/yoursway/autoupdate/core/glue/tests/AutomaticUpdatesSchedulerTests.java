package com.yoursway.autoupdate.core.glue.tests;

import static com.yoursway.autoupdate.core.glue.tests.Variable.anyObjectSavingInto;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import org.junit.Before;
import org.junit.Test;

import com.yoursway.autoupdate.core.glue.AutomaticUpdatesScheduler;
import com.yoursway.autoupdate.core.glue.RunnableWithTime;
import com.yoursway.autoupdate.core.glue.UpdateTimingConfiguration;
import com.yoursway.autoupdate.core.glue.UpdateTimingConfigurationListener;
import com.yoursway.autoupdate.core.glue.sheduling.Scheduler;
import com.yoursway.autoupdate.core.glue.state.overall.OverallState;
import com.yoursway.autoupdate.core.glue.state.overall.OverallStateListener;

public class AutomaticUpdatesSchedulerTests {
    
    private Scheduler scheduler;
    private UpdateTimingConfiguration timing;
    private OverallState overallState;
    private AutomaticUpdatesScheduler aus;
    private Variable<RunnableWithTime> scheduledRunnable;
    
    @Before
    public void setUp() {
        scheduler = createMock(Scheduler.class);
        timing = createMock(UpdateTimingConfiguration.class);
        overallState = createMock(OverallState.class);
        
        timing.addListener((UpdateTimingConfigurationListener) anyObject());
        overallState.addListener((OverallStateListener) anyObject());
        replayAll();
        aus = new AutomaticUpdatesScheduler(scheduler, timing, overallState);
        verifyAll();
        
        scheduledRunnable = new Variable<RunnableWithTime>(RunnableWithTime.class);
    }
    
    protected void replayAll() {
        replay(timing, scheduler, overallState);
    }
    
    protected void resetAll() {
        reset(timing, scheduler, overallState);
    }
    
    protected void verifyAll() {
        verify(timing, scheduler, overallState);
        resetAll();
    }
    
    @Test
    public void initialScheduling() {
        timing.nextAutomaticUpdateTime();
        expectLastCall().andReturn(1200l);
        scheduler.schedule(anyObjectSavingInto(scheduledRunnable), eq(1200l));
        replayAll();
        
        aus.applicationStarted(1100);
        verifyAll();
    }
    
    @Test
    public void initialCheck() {
        initialScheduling();
        
        overallState.startCheckingForUpdatesAutomatically(1300);
        expectLastCall().andReturn(true);
        replayAll();
        
        scheduledRunnable.extractValue().run(1300);
        verifyAll();
    }
    
    @Test
    public void subsequentScheduling() {
        initialCheck();
        
        timing.nextAutomaticUpdateTime();
        expectLastCall().andReturn(1500l);
        scheduler.schedule(anyObjectSavingInto(scheduledRunnable), eq(1500l));
        replayAll();
        
        ((UpdateTimingConfigurationListener) aus).nextAutomaticUpdateTimeChanged(1450);
        verifyAll();
    }
    
    @Test
    public void subsequentCheck() {
        subsequentScheduling();
        
        overallState.startCheckingForUpdatesAutomatically(1500);
        expectLastCall().andReturn(true);
        replayAll();
        
        scheduledRunnable.extractValue().run(1500);
        verifyAll();
    }
    
    @Test
    public void cannotStartInitialCheck() {
        initialScheduling();
        
        overallState.startCheckingForUpdatesAutomatically(1300);
        expectLastCall().andReturn(false);
        replayAll();
        
        scheduledRunnable.extractValue().run(1300);
        verifyAll();
    }
    
}
