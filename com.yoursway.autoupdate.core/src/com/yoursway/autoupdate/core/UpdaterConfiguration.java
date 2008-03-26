package com.yoursway.autoupdate.core;

import org.eclipse.core.runtime.Assert;

import com.yoursway.autoupdate.core.filespec.FileSetSpec;
import com.yoursway.autoupdate.core.path.Path;

public class UpdaterConfiguration {
	
	private final FileSetSpec updaterFiles;
    private final Path updaterJar;

	public UpdaterConfiguration(FileSetSpec updaterFiles, Path updaterJar) {
	    Assert.isNotNull(updaterFiles);
	    Assert.isNotNull(updaterJar);
		this.updaterFiles = updaterFiles;
        this.updaterJar = updaterJar;
	}
	
	public FileSetSpec updaterFiles() {
		return updaterFiles;
	}
	
	public Path updaterJar() {
        return updaterJar;
    }

	public ReplaceStrategy replaceStrategy(Path file) {
		return ReplaceStrategy.REPLACE_AFTER_SHUTDOWN;
	}

	public final boolean isPartOfUpdater(Path file) {
		return updaterFiles.contains(file);
	}
	
}
