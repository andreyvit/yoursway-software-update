package com.yoursway.autoupdate.ui;

public interface UpdatePreferencesCallback {
    
    void setSchedule(Schedule schedule);
    
    void checkNow();
    
}
