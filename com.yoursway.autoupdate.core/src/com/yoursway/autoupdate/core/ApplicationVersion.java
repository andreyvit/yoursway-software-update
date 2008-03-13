package com.yoursway.autoupdate.core;

public class ApplicationVersion {

	private final String version;
	private final String displayName;

	public ApplicationVersion(String version, String displayName) {
		this.version = version;
		this.displayName = displayName;
	}

	public String versionString() {
		return version;
	}
	
	public String displayName() {
		return displayName;
	}
	
}
