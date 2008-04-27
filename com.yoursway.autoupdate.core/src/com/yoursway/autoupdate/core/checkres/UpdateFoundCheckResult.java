package com.yoursway.autoupdate.core.checkres;

import com.yoursway.autoupdate.core.ProposedUpdate;

public class UpdateFoundCheckResult extends CheckResultImpl {
    
    private final ProposedUpdate update;

    public UpdateFoundCheckResult(ProposedUpdate update) {
        if (update == null)
            throw new NullPointerException("update is null");
        this.update = update;
    }
    
    public ProposedUpdate proposedUpdate() {
        return update;
    }
    
    @Override
    public ProposedUpdate foundUpdateOrNull() {
        return update;
    }

    @Override
    public String toString() {
        return "UPDATE_FOUND";
    }

    public boolean isSuccess() {
        return true;
    }

    public void accept(CheckResultVisitor visitor) {
        visitor.updateFound(this);
    }
    
    public boolean updatesFound() {
        return true;
    }

}
