package com.yoursway.autoupdate.core.execution;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.yoursway.autoupdate.core.actions.Action;
import com.yoursway.autoupdate.core.actions.EclipseStartInfo;
import com.yoursway.autoupdate.core.actions.Executor;
import com.yoursway.utils.YsFileUtils;

public class RealExecutor implements Executor {
    
    public void copy(File source, File destination) {
        try {
            YsFileUtils.fileCopy(source, destination);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFile(File file) {
        YsFileUtils.deleteFile(file);
    }

    public void deleteRecursively(File directory) {
        YsFileUtils.deleteRecursively(directory);
    }

    public void restartIntoUpdater(File workingDir, File jar, Collection<Action> actions) {
        System.out.println("RealExecutor.restartIntoUpdater()");
    }

    public void startMainEclipse(EclipseStartInfo info, List<Action> pendingActions) {
        System.out.println("RealExecutor.startMainEclipse()");
    }
    
}
