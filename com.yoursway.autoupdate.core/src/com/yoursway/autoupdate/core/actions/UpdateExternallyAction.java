package com.yoursway.autoupdate.core.actions;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class UpdateExternallyAction implements Action {
    
    private static final long serialVersionUID = 1L;
    
    private final Collection<Action> fileActions;
    private final File workingDir;
    private final File jar;

    private final Collection<File> updaterFiles;

    public UpdateExternallyAction(File workingDir, File jar, Collection<File> updaterFiles, Collection<Action> fileActions) {
        this.workingDir = workingDir;
        this.jar = jar;
        this.updaterFiles = updaterFiles;
        this.fileActions = fileActions;
    }

    public void execute(Executor executor) throws IOException {
        ((Executor42) executor).restartIntoUpdater(workingDir, jar, updaterFiles, fileActions);
    }
    
}
