package com.yoursway.autoupdate.core.actions;

import java.util.List;

import com.yoursway.autoupdate.core.Action;
import com.yoursway.autoupdate.core.EclipseStartInfo;
import com.yoursway.autoupdate.core.Executor;

public class StartMainEclipseAction implements Action {
    
    private final List<Action> pendingActions;
    private final EclipseStartInfo info;

    public StartMainEclipseAction(EclipseStartInfo info, List<Action> pendingActions) {
        this.info = info;
        this.pendingActions = pendingActions;
    }

    public void execute(Executor executor) {
        executor.startMainEclipse(info, pendingActions);
    }
    
}
