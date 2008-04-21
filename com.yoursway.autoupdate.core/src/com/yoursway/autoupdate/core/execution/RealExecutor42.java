package com.yoursway.autoupdate.core.execution;

import static com.yoursway.utils.YsFileUtils.transfer;
import static com.yoursway.utils.YsFileUtils.writeObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.yoursway.autoupdate.core.actions.Action;
import com.yoursway.autoupdate.core.actions.Executor42;
import com.yoursway.autoupdate.internal.launching.IVMRunner;
import com.yoursway.autoupdate.internal.launching.VMRunnerConfiguration;
import com.yoursway.autoupdate.launching.ILaunch;
import com.yoursway.autoupdate.launching.VmRunners;

public class RealExecutor42 extends RealExecutor implements Executor42 {

    private static class MyLaunch implements ILaunch {

        private Process process;

        public void setProcess(Process process) {
            this.process = process;
        }

        public Process process() {
            return process;
        }

    }

    public void restartIntoUpdater(File workingDir, File jar, Collection<File> updaterFiles, Collection<Action> actions) throws IOException {
        File jobFile = File.createTempFile("updaterjob", "");
        writeObject(actions, jobFile);
        List<String> classPath = new ArrayList<String>();
        classPath.add(jar.toString());
        for (File file : updaterFiles)
            if (file.getName().endsWith(".jar"))
                classPath.add(file.toString());
        String[] cp = classPath.toArray(new String[classPath.size()]);
        VMRunnerConfiguration config = new VMRunnerConfiguration("com.yoursway.autoupdate.core.extupdater.Main", cp);
        config.setProgramArguments(new String[] {jobFile.toString()});
        IVMRunner runner = VmRunners.createRunner();
        MyLaunch launch = new MyLaunch();
        try {
            runner.run(config, launch, new NullProgressMonitor());
        } catch (CoreException e) {
            throw (IOException) new IOException().initCause(e);
        }
        Process process = launch.process();
        InputStream inputStream = process.getInputStream();
        ByteArrayOutputStream xxx = new ByteArrayOutputStream();
        String magic = "GOODBYE CRUEL WORLD";
        byte[] magicBytes = magic.getBytes("utf-8");
        transfer(inputStream, xxx, magicBytes.length);
        String string = xxx.toString("utf-8");
        if (string.startsWith(magic))
            System.exit(0);
        else {
            process.destroy();
            throw new RuntimeException("Bad updater response, probably launch error: " + string);
        }
    }

}
