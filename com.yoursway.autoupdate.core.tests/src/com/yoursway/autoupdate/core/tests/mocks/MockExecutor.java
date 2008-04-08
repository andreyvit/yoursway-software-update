package com.yoursway.autoupdate.core.tests.mocks;

import static com.google.common.base.Join.join;

import java.io.File;
import java.util.Collection;
import java.util.List;

import com.yoursway.autoupdate.core.actions.Action;
import com.yoursway.autoupdate.core.actions.EclipseStartInfo;
import com.yoursway.autoupdate.core.actions.Executor;
import com.yoursway.autoupdate.core.actions.RemoteSource;
import com.yoursway.autoupdate.core.versions.definitions.RemoteFile;
import com.yoursway.utils.relativepath.RelativePath;

public class MockExecutor implements Executor {
    
    private int nextTempDir = 1;
    
    private StringBuilder result = new StringBuilder();
    
    public File createTemporaryDirectory() {
        return new File(("/tmp/dir" + nextTempDir++));
    }
    
    public void restartIntoUpdater(File workingDir, File jar, Collection<Action> actions) {
        result.append("RESTART FROM " + workingDir + ", EXEC " + jar + " AND DO:\n");
        serializeActions(actions);
    }
    
    private void serializeActions(Collection<Action> actions) {
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
    
    public File download(RemoteSource remote, RelativePath path) {
        return new File(new File("/tmp/download"), remote.url().getPath());
    }
    
    public void deleteFile(File file) {
        result.append("DELETE ").append(file).append("\n");
    }
    
    public EclipseStartInfo determineCurrentEclipseStartInfo() {
        return new EclipseStartInfo() {
            
            @Override
            public String toString() {
                return "MockEclipseStartInfo";
            }
            
        };
    }
    
    public void startMainEclipse(EclipseStartInfo info, List<Action> pendingActions) {
        result.append("START MAIN ECLIPSE (" + info + ") AND DO:\n");
        serializeActions(pendingActions);
    }
    
    public void deleteRecursively(File directory) {
        result.append("RM -RF ").append(directory).append("\n");
    }
    
}
