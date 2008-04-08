package com.yoursway.autoupdate.core.execution;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.yoursway.autoupdate.core.actions.Action;
import com.yoursway.autoupdate.core.actions.EclipseStartInfo;
import com.yoursway.autoupdate.core.actions.Executor;
import com.yoursway.autoupdate.core.actions.RemoteSource;
import com.yoursway.utils.YsFileUtils;
import com.yoursway.utils.relativepath.RelativePath;

public class RealExecutor implements Executor {
    
    private File downloadDir = null;

    public void copy(File source, File destination) {
        try {
            YsFileUtils.fileCopy(source, destination);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public File createTemporaryDirectory() {
        try {
            return YsFileUtils.createTempFolder("updater", "tmp");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFile(File file) {
        if (file.exists() && !file.delete())
            throw new RuntimeException("Cannot delete file " + file);
    }

    public void deleteRecursively(File directory) {
        File[] children = directory.listFiles();
        if (children != null) {
            for (File child : children)
                if (child.isDirectory())
                    deleteRecursively(child);
                else 
                    deleteFile(child);
            
            if (!directory.delete())
                throw new RuntimeException("Cannot delete directory " + directory);
        }
    }

    public EclipseStartInfo determineCurrentEclipseStartInfo() {
        return new RealEclipseStartupInfo();
    }

    public File download(RemoteSource remote, RelativePath path) {
        if (downloadDir == null)
            downloadDir = createTemporaryDirectory();
        try {
            File temp = path.toFile(downloadDir);
            YsFileUtils.download(remote.url(), temp);
            return temp;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void restartIntoUpdater(File workingDir, File jar, Collection<Action> actions) {
    }

    public void startMainEclipse(EclipseStartInfo info, List<Action> pendingActions) {
    }
    
}
