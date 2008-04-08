package com.yoursway.autoupdate.core.versions.definitions;

public class VersionDefinitionNotAvailable extends VersionDefinitionException {

	private static final long serialVersionUID = 1L;

	public VersionDefinitionNotAvailable() {
		super();
	}

	public VersionDefinitionNotAvailable(String message, Throwable cause) {
		super(message, cause);
	}

	public VersionDefinitionNotAvailable(String message) {
		super(message);
	}

	public VersionDefinitionNotAvailable(Throwable cause) {
		super(cause);
	}

}
