package com.yoursway.autoupdate.core.extupdater;

import static com.yoursway.autoupdate.core.extupdater.Join.join;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.yoursway.autoupdate.core.actions.Action;
import com.yoursway.autoupdate.core.actions.EclipseStartInfo;
import com.yoursway.autoupdate.core.actions.Executor;

public class MockExecutor implements Executor {
    
    private int nextTempDir = 1;
    
    private StringBuilder result = new StringBuilder();
    
    public File createTemporaryDirectory() {
        return new File(("/tmp/dir" + nextTempDir++));
    }
    
    public void restartIntoUpdater(File workingDir, File jar, Collection<File> updaterFiles, Collection<Action> actions) throws IOException {
        result.append("RESTART FROM " + workingDir + ", EXEC " + jar + " AND DO:\n");
        serializeActions(actions);
    }
    
    private void serializeActions(Collection<Action> actions) throws IOException {
        MockExecutor subexec = new MockExecutor();
        for (Action action : actions)
            action.execute(subexec);
        result.append(join("\n- ", ("\n" + subexec.toString().trim()).split("\n")).trim()).append("\n");
    }
    
    @Override
    public String toString() {
        return result.toString().trim();
    }
    
    public void copy(File source, File destination) {
        result.append("COPY " + source + " TO " + destination + "\n");
    }
    
    public void deleteFile(File file) {
        result.append("DELETE ").append(file).append("\n");
    }
    
    public void startMainEclipse(EclipseStartInfo info, List<Action> pendingActions) throws IOException {
        result.append("START MAIN ECLIPSE (" + info + ") AND DO:\n");
        serializeActions(pendingActions);
    }
    
    public void deleteRecursively(File directory) {
        result.append("RM -RF ").append(directory).append("\n");
    }
    
}
