package com.yoursway.autoupdate.core.versions.definitions;

public class InvalidVersionDefinitionException extends VersionDefinitionException {

	private static final long serialVersionUID = 1L;

	public InvalidVersionDefinitionException() {
		super();
	}

	public InvalidVersionDefinitionException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidVersionDefinitionException(String message) {
		super(message);
	}

	public InvalidVersionDefinitionException(Throwable cause) {
		super(cause);
	}

}
