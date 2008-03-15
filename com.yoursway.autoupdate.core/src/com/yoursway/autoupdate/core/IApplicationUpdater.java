package com.yoursway.autoupdate.core;

public interface IApplicationUpdater {

	VersionDefinition latestUpdateFor(ApplicationVersion currentVersion) throws UpdateLoopException;

	VersionDefinition nextUpdateFor(ApplicationVersion currentVersion);

	boolean freshUpdatesAvailable(ApplicationVersion currentVersion);

	ApplicationVersion[] availableVersions(ApplicationVersion currentVersion);

	VersionDefinition updateToVersion(ApplicationVersion current,
			ApplicationVersion target);

}
