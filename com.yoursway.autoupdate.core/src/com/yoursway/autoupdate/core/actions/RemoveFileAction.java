/**
 * 
 */
package com.yoursway.autoupdate.core.actions;

import java.io.File;

import com.yoursway.autoupdate.core.Action;
import com.yoursway.autoupdate.core.Executor;

public final class RemoveFileAction implements Action {
	
	private final File file;

    public RemoveFileAction(File file) {
        this.file = file;
	}

    @Override
    public String toString() {
        return "DELETE " + file;
    }

    public void execute(Executor executor) {
        executor.deleteFile(file);
    }
	
}
