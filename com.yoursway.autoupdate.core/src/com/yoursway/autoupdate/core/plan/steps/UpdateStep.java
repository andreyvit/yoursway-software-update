package com.yoursway.autoupdate.core.plan.steps;

import java.util.List;

import com.yoursway.autoupdate.core.UpdateRequest;
import com.yoursway.autoupdate.core.actions.Action;

public interface UpdateStep {
    
    void createActions(UpdateRequest request, List<Action> storeInto);

}
