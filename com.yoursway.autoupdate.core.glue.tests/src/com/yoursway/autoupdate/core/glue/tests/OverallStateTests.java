package com.yoursway.autoupdate.core.glue.tests;

import static com.yoursway.autoupdate.core.glue.state.overall.Mode.AUTOMATIC_CHECK;
import static com.yoursway.autoupdate.core.glue.state.overall.Mode.DISABLED;
import static com.yoursway.autoupdate.core.glue.state.overall.Mode.MANUAL_CHECK;
import static com.yoursway.autoupdate.core.glue.state.overall.Mode.NO_UPDATES;
import static com.yoursway.autoupdate.core.glue.state.overall.Mode.UPDATE_FOUND_ACTIONS_UNDECIDED;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.yoursway.autoupdate.core.glue.checkres.CommunicationErrorCheckResult;
import com.yoursway.autoupdate.core.glue.checkres.InternalFailureCheckResult;
import com.yoursway.autoupdate.core.glue.checkres.NoUpdatesCheckResult;
import com.yoursway.autoupdate.core.glue.checkres.NoWriteAccessCheckResult;
import com.yoursway.autoupdate.core.glue.checkres.UpdateFoundCheckResult;
import com.yoursway.autoupdate.core.glue.state.overall.OverallStateImpl;

public class OverallStateTests {
    
    private OverallStateImpl os;

    @Before
    public void setUp() {
        os = new OverallStateImpl();
    }
    
    @Test
    public void initiallyInNoUpdates() {
        assertEquals(NO_UPDATES, os.mode());
    }
    
    @Test
    public void recordsStartupTime() {
        os.startup(123);
        assertEquals(123l, os.startUpTime());
        assertEquals(NO_UPDATES, os.mode());
    }
    
    @Test
    public void recordsFirstRunTime() {
        os.startup(123);
        assertEquals(123l, os.firstRunTime());
    }
    
    @Test
    public void startsManualCheck() {
        boolean res = os.startCheckingForUpdatesManually(300);
        assertEquals(true, res);
        assertEquals(MANUAL_CHECK, os.mode());
        assertEquals(300, os.lastCheckAttemptTime());
    }
    
    @Test
    public void startsAutomaticCheck() {
        boolean res = os.startCheckingForUpdatesAutomatically(300);
        assertEquals(true, res);
        assertEquals(AUTOMATIC_CHECK, os.mode());
        assertEquals(300, os.lastCheckAttemptTime());
    }
    
    @Test
    public void deniesRecursiveAutomaticCheck() {
        startsManualCheck();
        boolean res = os.startCheckingForUpdatesAutomatically(400);
        assertEquals(false, res);
        assertEquals(MANUAL_CHECK, os.mode());
    }
    
    @Test
    public void deniesRecursiveManualCheck() {
        startsAutomaticCheck();
        boolean res = os.startCheckingForUpdatesManually(400);
        assertEquals(false, res);
        assertEquals(AUTOMATIC_CHECK, os.mode());
    }
    
    @Test
    public void interpretsNoWriteAccessResult() {
        startsAutomaticCheck();
        os.finishedCheckingForUpdates(500, new NoWriteAccessCheckResult());
        assertEquals(DISABLED, os.mode());
        assertEquals(true, os.lastCheckAttempt().hasFailed());
    }
    
    @Test
    public void interpretsCommunicationErrorResult() {
        startsAutomaticCheck();
        os.finishedCheckingForUpdates(500, new CommunicationErrorCheckResult());
        assertEquals(NO_UPDATES, os.mode());
        assertEquals(true, os.lastCheckAttempt().hasFailed());
    }
    
    @Test
    public void interpretsInternalFailureResult() {
        startsAutomaticCheck();
        os.finishedCheckingForUpdates(500, new InternalFailureCheckResult(new AssertionError("foo")));
        assertEquals(NO_UPDATES, os.mode());
        assertEquals(true, os.lastCheckAttempt().hasFailed());
    }
    
    @Test
    public void interpretsNoUpdatesResult() {
        startsAutomaticCheck();
        os.finishedCheckingForUpdates(500, new NoUpdatesCheckResult());
        assertEquals(NO_UPDATES, os.mode());
        assertEquals(false, os.lastCheckAttempt().hasFailed());
        assertEquals(300, os.lastCheckAttempt().time());
    }
    
    @Test
    public void interpretsUpdateFoundResult() {
        startsAutomaticCheck();
        os.finishedCheckingForUpdates(500, new UpdateFoundCheckResult());
        assertEquals(UPDATE_FOUND_ACTIONS_UNDECIDED, os.mode());
        assertEquals(false, os.lastCheckAttempt().hasFailed());
        assertEquals(300, os.lastCheckAttempt().time());
    }
    
}
