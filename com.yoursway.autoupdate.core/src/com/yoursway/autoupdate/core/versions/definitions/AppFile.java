package com.yoursway.autoupdate.core.versions.definitions;

import java.io.Serializable;

import org.eclipse.core.runtime.Assert;

import com.google.common.base.Function;
import com.yoursway.utils.relativepath.Pathes;
import com.yoursway.utils.relativepath.RelativePath;

public class AppFile implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected final RelativePath relativePath;
	protected final String md5;
    public static final Function<AppFile, RelativePath> APPFILE_TO_PATH = new Function<AppFile, RelativePath>() {
        
        public RelativePath apply(AppFile from) {
            return from.relativePath();
        }
        
    };

	public AppFile(RelativePath relativePath, String md5) {
		Assert.isNotNull(relativePath);
		Assert.isNotNull(md5);
		this.md5 = md5;
		this.relativePath = relativePath;
	}

	public RelativePath relativePath() {
		return relativePath;
	}

	public String md5() {
		return md5;
	}

	@Override
	public String toString() {
		return relativePath + "#" + md5;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((md5 == null) ? 0 : md5.hashCode());
		result = prime * result + ((relativePath == null) ? 0 : relativePath.hashCode());
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
		if (relativePath == null) {
			if (other.relativePath != null)
				return false;
		} else if (!relativePath.equals(other.relativePath))
			return false;
		return true;
	}

    public static AppFile appFile(String path, String md5) {
        return new AppFile(Pathes.relativePath(path), md5);
    }

}