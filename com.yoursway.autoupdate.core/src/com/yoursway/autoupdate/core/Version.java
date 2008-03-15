package com.yoursway.autoupdate.core;

/**
 * This class encapsulates a String version ID, in case we want to turn it into
 * something more interesting in the future.
 */
public class Version {

	private final String version;

	public Version(String version) {
		this.version = version;
	}

	public String versionString() {
		return version;
	}
	
	@Override
	public String toString() {
		return version;
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
		Version other = (Version) obj;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

}
