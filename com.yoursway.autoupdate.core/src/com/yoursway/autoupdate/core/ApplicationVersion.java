package com.yoursway.autoupdate.core;

public class ApplicationVersion {

	private final String version;
	private final String displayName;

	public ApplicationVersion(String version, String displayName) {
		this.version = version;
		this.displayName = displayName;
	}

	public String versionString() {
		return version;
	}

	public String displayName() {
		return displayName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ApplicationVersion other = (ApplicationVersion) obj;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

}
