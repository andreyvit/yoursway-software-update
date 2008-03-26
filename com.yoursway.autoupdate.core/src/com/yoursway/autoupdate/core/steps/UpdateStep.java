package com.yoursway.autoupdate.core.steps;

import java.util.List;

import com.yoursway.autoupdate.core.Action;
import com.yoursway.autoupdate.core.UpdateRequest;

public interface UpdateStep {
    
    void createActions(UpdateRequest request, List<Action> storeInto);

}
