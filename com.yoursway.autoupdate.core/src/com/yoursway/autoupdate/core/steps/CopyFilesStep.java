package com.yoursway.autoupdate.core.steps;

import com.yoursway.autoupdate.core.dirs.Directory;
import com.yoursway.autoupdate.core.filespec.FileSetSpec;

public class CopyFilesStep implements UpdateStep {

	private final FileSetSpec fileset;
	private final Directory target;

	public CopyFilesStep(FileSetSpec fileset, Directory target) {
		this.fileset = fileset;
		this.target = target;
	}
	
	@Override
	public String toString() {
		return "COPY " + fileset + " INTO " + target;
	}

}
