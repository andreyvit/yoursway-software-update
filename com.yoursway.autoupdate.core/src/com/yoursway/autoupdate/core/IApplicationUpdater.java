package com.yoursway.autoupdate.core;

public interface IApplicationUpdater {

	VersionDefinition latestUpdateFor(Version currentVersion) throws UpdateLoopException;

	VersionDefinition nextUpdateFor(Version currentVersion);

	boolean freshUpdatesAvailable(Version currentVersion);

	Version[] availableVersions(Version currentVersion);

	VersionDefinition updateToVersion(Version current,
			Version target);

}
