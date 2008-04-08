package com.yoursway.autoupdate.core.actions.concrete;

import java.io.File;
import java.util.Collection;

import com.yoursway.autoupdate.core.actions.Action;
import com.yoursway.autoupdate.core.actions.Executor;

public class UpdateExternallyAction implements Action {
    
    private final Collection<Action> fileActions;
    private final File workingDir;
    private final File jar;

    public UpdateExternallyAction(File workingDir, File jar, Collection<Action> fileActions) {
        this.workingDir = workingDir;
        this.jar = jar;
        this.fileActions = fileActions;
    }

    public void execute(Executor executor) {
        executor.restartIntoUpdater(workingDir, jar, fileActions);
    }
    
}
