package com.yoursway.autoupdate.core;

import java.io.File;

import com.yoursway.autoupdate.core.actions.EclipseStartInfo;
import com.yoursway.autoupdate.core.actions.RemoteSource;
import com.yoursway.utils.relativepath.RelativePath;

public interface Executor9 {
    
    File createTemporaryDirectory();
    
    EclipseStartInfo determineCurrentEclipseStartInfo();
    
    File download(RemoteSource remote, RelativePath relativePath);

}
