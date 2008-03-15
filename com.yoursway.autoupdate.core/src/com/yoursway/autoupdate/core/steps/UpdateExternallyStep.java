package com.yoursway.autoupdate.core.steps;

import com.yoursway.autoupdate.core.dirs.Directory;
import com.yoursway.autoupdate.core.filespec.FileSetSpec;

public class UpdateExternallyStep implements UpdateStep {

	private final Directory updaterLocation;
	private final FileSetSpec files;

	public UpdateExternallyStep(Directory updaterLocation, FileSetSpec files) {
		this.updaterLocation = updaterLocation;
		this.files = files;
	}
	
	@Override
	public String toString() {
		return "RESTART FROM " + updaterLocation + " UPDATE " + files; 
	}
	
}
