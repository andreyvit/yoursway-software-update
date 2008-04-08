package com.yoursway.autoupdate.core.versions.definitions;

import org.eclipse.core.runtime.Assert;

import com.yoursway.autoupdate.core.actions.RemoteSource;
import com.yoursway.utils.relativepath.RelativePath;

public class RemoteFile extends AppFile {

	private final RemoteSource source;

    public RemoteFile(RelativePath relativePath, String md5, RemoteSource source) {
		super(relativePath, md5);
		Assert.isNotNull(source);
		this.source = source;
	}
    
    public RemoteSource source() {
        return source;
    }

}
