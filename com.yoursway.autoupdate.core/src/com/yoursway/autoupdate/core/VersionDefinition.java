package com.yoursway.autoupdate.core;

public class VersionDefinition {

	private final ApplicationFile[] files;
	private final Version version;
	private final String changes;
	private final String displayName;

	public VersionDefinition(Version version, String changes,
			String displayName, ApplicationFile[] files) {
		this.version = version;
		this.changes = changes;
		this.displayName = displayName;
		this.files = files;
	}

	public Version version() {
		return version;
	}

	public String changesDescription() {
		return changes;
	}

	public ApplicationFile[] files() {
		return files;
	}

	
	
}
