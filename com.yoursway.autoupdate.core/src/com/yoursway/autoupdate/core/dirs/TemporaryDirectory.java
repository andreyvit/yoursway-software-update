package com.yoursway.autoupdate.core.dirs;

public abstract class TemporaryDirectory implements Directory {
	
	private final String debugName;

	TemporaryDirectory(String debugName) {
		this.debugName = debugName;
	}
	
	@Override
	public String toString() {
		return debugName;
	}

}
