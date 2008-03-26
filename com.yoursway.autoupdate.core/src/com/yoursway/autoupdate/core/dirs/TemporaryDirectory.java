package com.yoursway.autoupdate.core.dirs;

import java.io.File;

public abstract class TemporaryDirectory implements Directory {
	
	private final String debugName;

	TemporaryDirectory(String debugName) {
		this.debugName = debugName;
	}
	
	public File resolve(DirectoryResolver resolver) {
	    return resolver.resolveTemporaryDirectory();
	}
	
	public boolean isTemporary() {
	    return true;
	}
	
	@Override
	public String toString() {
		return debugName;
	}

}
