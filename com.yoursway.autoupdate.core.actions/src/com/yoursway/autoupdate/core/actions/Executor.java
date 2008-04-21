package com.yoursway.autoupdate.core.actions;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface Executor {

    void copy(File source, File destination);

    void deleteFile(File file);

    void startMainEclipse(EclipseStartInfo info, List<Action> pendingActions) throws IOException;

    void deleteRecursively(File directory);
	
}
