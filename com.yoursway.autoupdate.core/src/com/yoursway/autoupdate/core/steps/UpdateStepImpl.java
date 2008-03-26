package com.yoursway.autoupdate.core.steps;

import com.yoursway.autoupdate.core.Action;
import com.yoursway.autoupdate.core.UpdateRequest;

public abstract class UpdateStepImpl {
    
    protected abstract Action instantiate(UpdateRequest request);
    
}
