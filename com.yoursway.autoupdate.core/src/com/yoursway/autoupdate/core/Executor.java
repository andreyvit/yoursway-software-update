package com.yoursway.autoupdate.core;

import java.io.File;
import java.util.Collection;
import java.util.List;

import com.yoursway.autoupdate.core.versiondef.RemoteFile;

public interface Executor {
	
	File createTemporaryDirectory();

	void restartIntoUpdater(File workingDir, File jar, Collection<Action> actions);

    void copy(File source, File destination);

    File download(RemoteFile remote);

    void deleteFile(File file);

    void startMainEclipse(EclipseStartInfo info, List<Action> pendingActions);
    
    EclipseStartInfo determineCurrentEclipseStartInfo();

    void deleteRecursively(File directory);
	
}
