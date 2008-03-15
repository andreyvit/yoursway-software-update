package com.yoursway.autoupdate.core.steps;

import com.yoursway.autoupdate.core.filespec.FileSetSpec;

public class UpdateFilesStep implements UpdateStep {

	private final FileSetSpec fileset;

	public UpdateFilesStep(FileSetSpec fileset) {
		this.fileset = fileset;
	}
	
	@Override
	public String toString() {
		return "UPDATE " + fileset;
	}

}
