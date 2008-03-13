package com.yoursway.autoupdate.core;

import java.net.URL;

public class ApplicationFile {

	private final String eclipseBoundPath;
	private final String md5;
	private final URL remoteUrl;

	public ApplicationFile(String md5, String eclipseBoundPath, URL remoteUrl) {
		this.md5 = md5;
		this.eclipseBoundPath = eclipseBoundPath;
		this.remoteUrl = remoteUrl;
	}

	public String eclipseBoundPath() {
		return eclipseBoundPath;
	}

	public String md5() {
		return md5;
	}

	public URL remoteUrl() {
		return remoteUrl;
	}
	
	
	
}
