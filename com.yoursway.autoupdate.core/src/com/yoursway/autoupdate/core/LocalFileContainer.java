package com.yoursway.autoupdate.core;

import java.io.File;
import java.util.Collection;

import com.yoursway.autoupdate.core.path.Path;
import com.yoursway.autoupdate.core.versiondef.AppFile;

public class LocalFileContainer implements FileContainer {

	private final File applicationPath;

	public LocalFileContainer(File applicationPath) {
		this.applicationPath = applicationPath;
	}

	public Collection<Path> allFiles() {
		return null;
	}

	public AppFile resolve(Path path) {
		return null;
	}
	
}
