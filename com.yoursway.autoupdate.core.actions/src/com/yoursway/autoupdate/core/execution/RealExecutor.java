package com.yoursway.autoupdate.core.execution;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    
    public void startMainEclipse(EclipseStartInfo info, List<Action> pendingActions) {
        try {
            new FileOutputStream("/tmp/update-ran").close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        RealEclipseStartupInfo realInfo = (RealEclipseStartupInfo) info;
        File launcher = realInfo.getLauncher();
        try {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Runtime.getRuntime().exec(new String[] {launcher.toString()});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
