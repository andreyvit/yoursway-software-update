package com.yoursway.autoupdate.core.execution;

import java.io.File;

import com.yoursway.autoupdate.core.actions.EclipseStartInfo;

public class RealEclipseStartupInfo implements EclipseStartInfo {
    
    private final File launcher;
    
    public RealEclipseStartupInfo(File launcher) {
        if (launcher == null)
            throw new NullPointerException("launcher is null");
        this.launcher = launcher;
    }
    
    public File getLauncher() {
        return launcher;
    }
    
    private static final long serialVersionUID = 1L;
    
}
