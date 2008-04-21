package com.yoursway.autoupdate.core.actions.concrete;

import java.io.IOException;
import java.util.List;

import com.yoursway.autoupdate.core.actions.Action;
import com.yoursway.autoupdate.core.actions.EclipseStartInfo;
import com.yoursway.autoupdate.core.actions.Executor;

public class StartMainEclipseAction implements Action {
    
    private static final long serialVersionUID = 1L;

    private final List<Action> pendingActions;
    private final EclipseStartInfo info;

    public StartMainEclipseAction(EclipseStartInfo info, List<Action> pendingActions) {
        this.info = info;
        this.pendingActions = pendingActions;
    }

    public void execute(Executor executor) throws IOException {
        executor.startMainEclipse(info, pendingActions);
    }
    
}
