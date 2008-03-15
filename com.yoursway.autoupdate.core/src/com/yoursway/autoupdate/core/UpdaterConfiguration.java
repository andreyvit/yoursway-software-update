package com.yoursway.autoupdate.core;

import com.yoursway.autoupdate.core.filespec.FileSetSpec;
import com.yoursway.autoupdate.core.path.Path;

public class UpdaterConfiguration {
	
	private final FileSetSpec updaterFiles;

	public UpdaterConfiguration(FileSetSpec updaterFiles) {
		this.updaterFiles = updaterFiles;
	}
	
	public FileSetSpec updaterFiles() {
		return updaterFiles;
	}

	public ReplaceStrategy replaceStrategy(Path file) {
		return ReplaceStrategy.REPLACE_AFTER_SHUTDOWN;
	}

	public final boolean isPartOfUpdater(Path file) {
		return updaterFiles.contains(file);
	}
	
}
