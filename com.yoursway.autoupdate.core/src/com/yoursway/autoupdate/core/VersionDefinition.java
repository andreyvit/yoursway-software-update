package com.yoursway.autoupdate.core;

public class VersionDefinition {

	private final ApplicationFile[] files;
	private final ApplicationVersion version;
	private final String changes;
	private final String displayName;

	public VersionDefinition(ApplicationVersion version, String changes,
			String displayName, ApplicationFile[] files) {
		this.version = version;
		this.changes = changes;
		this.displayName = displayName;
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
