package com.yoursway.autoupdate.ui;

import com.yoursway.autoupdate.core.glue.state.schedule.Schedule;

public interface UpdatePreferencesCallback {
    
    void setSchedule(Schedule schedule);
    
    void checkNow();
    
}
