package com.yoursway.autoupdate.core.versiondef;

import java.net.URL;

import org.eclipse.core.runtime.Assert;

import com.yoursway.autoupdate.core.path.Path;

public class RemoteFile extends AppFile {

	private final URL remoteUrl;

	public RemoteFile(Path path, String md5, URL remoteUrl) {
		super(path, md5);
		Assert.isNotNull(remoteUrl);
		this.remoteUrl = remoteUrl;
	}

	public URL remoteUrl() {
		return remoteUrl;
	}

}
