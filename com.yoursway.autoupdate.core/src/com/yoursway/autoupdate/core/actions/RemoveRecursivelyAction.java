/**
 * 
 */
package com.yoursway.autoupdate.core.actions;

import java.io.File;

import com.yoursway.autoupdate.core.Action;
import com.yoursway.autoupdate.core.Executor;

public final class RemoveRecursivelyAction implements Action {
	
	private final File directory;

    public RemoveRecursivelyAction(File directory) {
        this.directory = directory;
	}

    @Override
    public String toString() {
        return "RM -RF " + directory;
    }

    public void execute(Executor executor) {
        executor.deleteRecursively(directory);
    }
	
}