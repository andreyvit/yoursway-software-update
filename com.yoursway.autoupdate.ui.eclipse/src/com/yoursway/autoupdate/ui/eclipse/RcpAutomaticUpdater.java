package com.yoursway.autoupdate.ui.eclipse;

import com.yoursway.autoupdate.ui.UpdatePreferencesComposite;

public interface RcpAutomaticUpdater {

    void checkForUpdates();

    void add(UpdatePreferencesComposite composite);
    
}
