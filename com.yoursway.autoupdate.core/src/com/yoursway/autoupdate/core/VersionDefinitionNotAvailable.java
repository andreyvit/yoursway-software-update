package com.yoursway.autoupdate.core;

public class VersionDefinitionNotAvailable extends Exception {

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
