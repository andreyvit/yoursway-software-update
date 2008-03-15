package com.yoursway.autoupdate.core;

public interface IVersionDefinitionLoader {

	VersionDefinition loadDefinition(Version currentVersion) throws VersionDefinitionNotAvailable;

}
