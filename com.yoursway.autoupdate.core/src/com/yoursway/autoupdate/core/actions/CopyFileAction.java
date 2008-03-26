package com.yoursway.autoupdate.core.actions;

import java.io.File;

import com.yoursway.autoupdate.core.Action;
import com.yoursway.autoupdate.core.Executor;

public class CopyFileAction implements Action {

    private final File source;
    private final File destination;

    public CopyFileAction(File source, File destination) {
        this.source = source;
        this.destination = destination;
    }

    public void execute(Executor executor) {
        executor.copy(source, destination);
    }
    
    public String toString() {
        return "COPY " + source + " INTO " + destination;
    }
    
}
