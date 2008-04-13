package com.yoursway.autoupdate.core.execution;

import java.io.File;
import java.io.IOException;

import com.yoursway.autoupdate.core.Executor9;
import com.yoursway.autoupdate.core.actions.EclipseStartInfo;
import com.yoursway.autoupdate.core.actions.RemoteSource;
import com.yoursway.utils.YsFileUtils;
import com.yoursway.utils.relativepath.RelativePath;

public class RealExecutor9 implements Executor9 {
    
    private File downloadDir = null;

    public File createTemporaryDirectory() {
        try {
            return YsFileUtils.createTempFolder("updater", "tmp");
        } catch (IOException e) {
            throw new RuntimeException(e);
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

}
