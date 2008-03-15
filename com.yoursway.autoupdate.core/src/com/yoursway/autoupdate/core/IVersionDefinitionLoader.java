package com.yoursway.autoupdate.core;

public interface IVersionDefinitionLoader {

	VersionDefinition latestUpdateFor(Version currentVersion) throws UpdateLoopException;

	VersionDefinition nextVersionFor(Version currentVersion);

	boolean newerVersionExists(Version currentVersion);

	Version[] availableVersions(Version currentVersion);

	VersionDefinition updateToVersion(Version current,
			Version target);

}
