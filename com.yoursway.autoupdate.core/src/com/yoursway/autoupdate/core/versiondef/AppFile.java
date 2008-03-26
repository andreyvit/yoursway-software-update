package com.yoursway.autoupdate.core.versiondef;

import static com.yoursway.autoupdate.core.path.Pathes.relativePath;

import org.eclipse.core.runtime.Assert;

import com.google.common.base.Function;
import com.yoursway.autoupdate.core.path.Path;

public class AppFile {

	protected final Path path;
	protected final String md5;
    public static final Function<AppFile, Path> APPFILE_TO_PATH = new Function<AppFile, Path>() {
        
        public Path apply(AppFile from) {
            return from.path();
        }
        
    };

	public AppFile(Path path, String md5) {
		Assert.isNotNull(path);
		Assert.isNotNull(md5);
		this.md5 = md5;
		this.path = path;
	}

	public Path path() {
		return path;
	}

	public String md5() {
		return md5;
	}

	@Override
	public String toString() {
		return path + "#" + md5;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((md5 == null) ? 0 : md5.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
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
		AppFile other = (AppFile) obj;
		if (md5 == null) {
			if (other.md5 != null)
				return false;
		} else if (!md5.equals(other.md5))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

    public static AppFile appFile(String path, String md5) {
        return new AppFile(relativePath(path), md5);
    }

}