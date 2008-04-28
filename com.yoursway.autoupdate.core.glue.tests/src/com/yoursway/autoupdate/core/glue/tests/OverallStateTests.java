package com.yoursway.autoupdate.core.glue.tests;

import static com.yoursway.autoupdate.core.glue.state.overall.Mode.AUTOMATIC_CHECK;
import static com.yoursway.autoupdate.core.glue.state.overall.Mode.DISABLED;
import static com.yoursway.autoupdate.core.glue.state.overall.Mode.MANUAL_CHECK;
import static com.yoursway.autoupdate.core.glue.state.overall.Mode.NO_UPDATES;
import static com.yoursway.autoupdate.core.glue.state.overall.Mode.UPDATE_FOUND_ACTIONS_UNDECIDED;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.yoursway.autoupdate.core.ProposedUpdate;
import com.yoursway.autoupdate.core.VersionDescription;
import com.yoursway.autoupdate.core.checkres.CommunicationErrorCheckResult;
import com.yoursway.autoupdate.core.checkres.InternalFailureCheckResult;
import com.yoursway.autoupdate.core.checkres.NoUpdatesCheckResult;
import com.yoursway.autoupdate.core.checkres.NoWriteAccessCheckResult;
import com.yoursway.autoupdate.core.checkres.UpdateFoundCheckResult;
import com.yoursway.autoupdate.core.glue.state.overall.OverallStateImpl;
import com.yoursway.autoupdate.core.versions.Version;

public class OverallStateTests {
    
    private OverallStateImpl os;

    @Before
    public void setUp() {
        os = new OverallStateImpl();
    }
    
    @Test
    public void initiallyInNoUpdates() {
        assertEquals(NO_UPDATES, os.state());
    }
    
    @Test
    public void recordsStartupTime() {
        os.startup(123);
        assertEquals(123l, os.startUpTime());
        assertEquals(NO_UPDATES, os.state());
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
        assertEquals(MANUAL_CHECK, os.state());
        assertEquals(300, os.lastCheckAttemptTime());
    }
    
    @Test
    public void startsAutomaticCheck() {
        boolean res = os.startCheckingForUpdatesAutomatically(300);
        assertEquals(true, res);
        assertEquals(AUTOMATIC_CHECK, os.state());
        assertEquals(300, os.lastCheckAttemptTime());
    }
    
    @Test
    public void deniesRecursiveAutomaticCheck() {
        startsManualCheck();
        boolean res = os.startCheckingForUpdatesAutomatically(400);
        assertEquals(false, res);
        assertEquals(MANUAL_CHECK, os.state());
    }
    
    @Test
    public void deniesRecursiveManualCheck() {
        startsAutomaticCheck();
        boolean res = os.startCheckingForUpdatesManually(400);
        assertEquals(false, res);
        assertEquals(AUTOMATIC_CHECK, os.state());
    }
    
    @Test
    public void interpretsNoWriteAccessResult() {
        startsAutomaticCheck();
        os.finishedCheckingForUpdates(500, new NoWriteAccessCheckResult());
        assertEquals(DISABLED, os.state());
        assertEquals(true, os.lastCheckAttempt().hasFailed());
    }
    
    @Test
    public void interpretsCommunicationErrorResult() {
        startsAutomaticCheck();
        os.finishedCheckingForUpdates(500, new CommunicationErrorCheckResult());
        assertEquals(NO_UPDATES, os.state());
        assertEquals(true, os.lastCheckAttempt().hasFailed());
    }
    
    @Test
    public void interpretsInternalFailureResult() {
        startsAutomaticCheck();
        os.finishedCheckingForUpdates(500, new InternalFailureCheckResult(new AssertionError("foo")));
        assertEquals(NO_UPDATES, os.state());
        assertEquals(true, os.lastCheckAttempt().hasFailed());
    }
    
    @Test
    public void interpretsNoUpdatesResult() {
        startsAutomaticCheck();
        os.finishedCheckingForUpdates(500, new NoUpdatesCheckResult());
        assertEquals(NO_UPDATES, os.state());
        assertEquals(false, os.lastCheckAttempt().hasFailed());
        assertEquals(300, os.lastCheckAttempt().time());
    }
    
    @Test
    public void interpretsUpdateFoundResult() {
        startsAutomaticCheck();
        os.finishedCheckingForUpdates(500, new UpdateFoundCheckResult(new ProposedUpdate() {

            public String changesDescription() {
                throw new UnsupportedOperationException();
            }

            public VersionDescription targetVersion() {
                throw new UnsupportedOperationException();
            }

        }));
        assertEquals(UPDATE_FOUND_ACTIONS_UNDECIDED, os.state());
        assertEquals(false, os.lastCheckAttempt().hasFailed());
        assertEquals(300, os.lastCheckAttempt().time());
    }
    
}
