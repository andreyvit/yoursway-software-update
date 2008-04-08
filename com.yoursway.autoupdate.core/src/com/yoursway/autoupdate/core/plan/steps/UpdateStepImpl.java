package com.yoursway.autoupdate.core.plan.steps;

import com.yoursway.autoupdate.core.UpdateRequest;
import com.yoursway.autoupdate.core.actions.Action;

public abstract class UpdateStepImpl {
    
    protected abstract Action instantiate(UpdateRequest request);
    
}
