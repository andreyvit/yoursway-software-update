package com.yoursway.autoupdate.core.actions;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public interface Executor42 extends Executor {

    void restartIntoUpdater(File workingDir, File jar, Collection<File> updaterFiles, Collection<Action> actions) throws IOException;

}
