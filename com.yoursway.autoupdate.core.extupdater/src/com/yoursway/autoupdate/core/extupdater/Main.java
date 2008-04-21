package com.yoursway.autoupdate.core.extupdater;

import static com.yoursway.utils.YsFileUtils.readAsObject;
import static com.yoursway.utils.YsFileUtils.writeString;

import java.io.File;
import java.util.Collection;

import com.yoursway.autoupdate.core.actions.Action;
import com.yoursway.autoupdate.core.actions.concrete.CopyFileAction;
import com.yoursway.autoupdate.core.actions.concrete.StartMainEclipseAction;
import com.yoursway.autoupdate.core.execution.RealExecutor;

public class Main {
    
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        if (args.length != 1)
            throw new IllegalArgumentException("Wrong number of arguments.");
        try {
            File jobFile = new File(args[0]);
            Collection<Action> actions = (Collection<Action>) readAsObject(jobFile);
            System.out.println("GOODBYE CRUEL WORLD");
            
            try {
                MockExecutor mock = new MockExecutor();
                for (Action action : actions)
                    action.execute(mock);
                writeString(new File("/tmp/updater-actions.txt"), mock.toString());
            } catch (Throwable e) {
                writeString(new File("/tmp/updater-actions.txt"), e.toString());
            }
            
            RealExecutor executor = new RealExecutor();
            for (Action action : actions)
                if (action instanceof StartMainEclipseAction || action instanceof CopyFileAction)
                    action.execute(executor);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
}
