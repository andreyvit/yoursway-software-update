/**
 * 
 */
package com.yoursway.autoupdate.core.tests.mocks;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.yoursway.autoupdate.core.tests.internal.Activator;
import com.yoursway.autoupdate.core.versions.definitions.UrlBasedVersionDefinitionLoader;

public final class LocalDirectoryUpdater extends
		UrlBasedVersionDefinitionLoader {
	
	private static final URL REP_URL;
	static {
		try {
			REP_URL = new URL("http://botva/");
		} catch (MalformedURLException e) {
			throw new AssertionError(e);
		}
	}
	
	private final String directoryName;

	public LocalDirectoryUpdater(String directoryName) {
		super(REP_URL);
		this.directoryName = directoryName;
	}

	@Override
	protected InputStream contentsFor(URL url) throws IOException {
		String path = url.toString().substring(REP_URL.toString().length());
		return Activator.openResource("tests/" + directoryName + "/" + path);
	}
}