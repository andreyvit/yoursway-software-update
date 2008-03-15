package com.yoursway.autoupdate.core;

public interface IApplicationUpdater {

	ApplicationUpdate latestUpdateFor(ApplicationVersion currentVersion) throws UpdateLoopException;

	ApplicationUpdate nextUpdateFor(ApplicationVersion currentVersion);

	boolean freshUpdatesAvailable(ApplicationVersion currentVersion);

	ApplicationVersion[] availableVersions(ApplicationVersion currentVersion);

	ApplicationUpdate updateToVersion(ApplicationVersion current,
			ApplicationVersion target);

}
