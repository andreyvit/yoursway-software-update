/**
 * 
 */
package com.yoursway.autoupdate.core.actions.concrete;

import java.io.File;

import com.yoursway.autoupdate.core.actions.Action;
import com.yoursway.autoupdate.core.actions.Executor;

public final class RemoveRecursivelyAction implements Action {
    
    private static final long serialVersionUID = 1L;

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