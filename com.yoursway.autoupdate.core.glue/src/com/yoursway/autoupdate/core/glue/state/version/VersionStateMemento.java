package com.yoursway.autoupdate.core.glue.state.version;

import com.yoursway.autoupdate.core.ProposedUpdate;

public class VersionStateMemento {
    
    final ProposedUpdate update;
    final VersionDecision decision;
    final long decidedAt;

    public VersionStateMemento(ProposedUpdate update, VersionDecision decision, long decidedAt) {
        this.update = update;
        this.decision = decision;
        this.decidedAt = decidedAt;
    }
    
}
