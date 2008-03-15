package com.yoursway.autoupdate.core;

import static java.util.Arrays.asList;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;

public class VersionDefinition {

	private final ApplicationFile[] files;
	private final Version version;
	private final String changes;
	private final String displayName;
	private final Version nextVersion;

	public VersionDefinition(Version version, String displayName,
			Version nextVersion, String changesDescription, ApplicationFile[] files) {
		Assert.isNotNull(version);
		Assert.isNotNull(displayName);
		Assert.isNotNull(files);
		this.version = version;
		this.nextVersion = nextVersion;
		this.changes = changesDescription;
		this.displayName = displayName;
		this.files = files;
	}

	public String displayName() {
		return displayName;
	}

	public Version version() {
		return version;
	}

	public String changesDescription() {
		return changes;
	}

	public Collection<ApplicationFile> files() {
		return asList(files);
	}
	
	public Version nextVersion() {
		return nextVersion;
	}
	
	public boolean hasNewerVersion() {
		return nextVersion != null;
	}

	@Override
	public String toString() {
		return version + " -> " + nextVersion;
	}
	
	
}
