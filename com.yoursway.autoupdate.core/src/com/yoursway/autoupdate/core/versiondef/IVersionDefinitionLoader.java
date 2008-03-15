package com.yoursway.autoupdate.core.versiondef;

public interface IVersionDefinitionLoader {

	VersionDefinition loadDefinition(Version currentVersion) throws VersionDefinitionNotAvailable;

}
