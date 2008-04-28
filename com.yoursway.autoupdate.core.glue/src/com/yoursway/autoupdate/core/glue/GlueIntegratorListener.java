package com.yoursway.autoupdate.core.glue;

import com.yoursway.autoupdate.core.ProposedUpdate;

public interface GlueIntegratorListener {
    
    void startedOrStoppedCheckingForUpdates();

    void askUserDecision(ProposedUpdate undecidedUpdate);

    void startedOrStoppedInstalling();
    
}
