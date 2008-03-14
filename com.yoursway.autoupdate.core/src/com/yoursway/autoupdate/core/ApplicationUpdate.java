package com.yoursway.autoupdate.core;

public class ApplicationUpdate {

	private final ApplicationFile[] files;
	private final ApplicationVersion version;
	private final String changes;

	public ApplicationUpdate(ApplicationVersion version, String changes,
			ApplicationFile[] files) {
		this.version = version;
		this.changes = changes;
		this.files = files;
	}

	public ApplicationVersion version() {
		return version;
	}

	public String changesDescription() {
		return changes;
	}

	public ApplicationFile[] files() {
		return files;
	}

	
	
}
