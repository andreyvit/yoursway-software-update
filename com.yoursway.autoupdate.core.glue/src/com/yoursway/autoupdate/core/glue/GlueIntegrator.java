package com.yoursway.autoupdate.core.glue;

import com.yoursway.autoupdate.core.glue.state.overall.Attempt;
import com.yoursway.autoupdate.core.glue.state.schedule.Schedule;

public interface GlueIntegrator {

    void addListener(GlueIntegratorListener listener);

    void removeListener(GlueIntegratorListener listener);

    Schedule getSchedule();

    void setSchedule(Schedule schedule);

    void checkForUpdates();

    boolean isCheckingForUpdates();

    Attempt getLastCheckAttemp();
    
}
