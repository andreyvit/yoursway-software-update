package com.yoursway.autoupdate.core.versions.definitions;

import com.yoursway.autoupdate.core.versions.Version;

public interface IVersionDefinitionLoader {

	VersionDefinition loadDefinition(Version currentVersion) throws VersionDefinitionNotAvailable, InvalidVersionDefinitionException;

}
