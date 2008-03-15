package com.yoursway.autoupdate.core;

import com.yoursway.autoupdate.core.versiondef.AppFile;

public class FileState {

	private final AppFile file;
	private final FileAction action;

	public FileState(AppFile file, FileAction action) {
		this.file = file;
		this.action = action;
	}
	
	public AppFile file() {
		return file;
	}
	
	public FileAction action() {
		return action;
	}
	
}
