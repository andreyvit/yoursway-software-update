/**
 * 
 */
package com.yoursway.autoupdate.core.tests.mocks;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.yoursway.autoupdate.core.HTTPBasedApplicationUpdater;
import com.yoursway.autoupdate.core.tests.Activator;

public final class LocalDirectoryUpdater extends
		HTTPBasedApplicationUpdater {
	
	private static final String REP_URL = "http://botva/";
	
	private final String directoryName;

	public LocalDirectoryUpdater(String directoryName) {
		super(REP_URL);
		this.directoryName = directoryName;
	}

	@Override
	protected InputStream contentsFor(URL url) throws IOException {
		String path = url.toString().substring(REP_URL.length());
		return Activator.openResource("tests/" + directoryName + "/" + path);
	}
}