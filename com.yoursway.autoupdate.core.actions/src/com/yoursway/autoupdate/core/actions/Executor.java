package com.yoursway.autoupdate.core.actions;

import java.io.File;
import java.util.Collection;
import java.util.List;

import com.yoursway.utils.relativepath.RelativePath;

public interface Executor {

	void restartIntoUpdater(File workingDir, File jar, Collection<Action> actions);

    void copy(File source, File destination);

    void deleteFile(File file);

    void startMainEclipse(EclipseStartInfo info, List<Action> pendingActions);

    void deleteRecursively(File directory);
	
}
