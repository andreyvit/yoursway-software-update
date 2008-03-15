package com.yoursway.autoupdate.core.tests.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Before;

import com.yoursway.autoupdate.core.IApplicationUpdater;
import com.yoursway.autoupdate.core.tests.Activator;
import com.yoursway.autoupdate.core.tests.mocks.LocalDirectoryUpdater;

public abstract class AbstractAutoUpdaterTestCase extends Assert {

	private IApplicationUpdater updater;

	@Before
	public void prepare() throws Exception {
		updater = new LocalDirectoryUpdater(getClass().getSimpleName());
	}
	
	protected IApplicationUpdater updater() {
		return updater;
	}

	protected static String joinPath(String c1, String c2) {
		IPath path = new Path(c1).append(c2);
		return path.toPortableString();
	}

	protected static String joinPath(String c1, String c2, String c3) {
		return joinPath(joinPath(c1, c2), c3);
	}

	protected static String readFile(final String fileName) throws IOException {
		InputStream in = Activator.openResource(fileName);
		return readAndClose(in);
	}

	public static String readAndClose(InputStream in) throws IOException {
		try {
			StringBuffer result = new StringBuffer();
			InputStreamReader reader = new InputStreamReader(in);
			char[] buf = new char[1024];
			while (true) {
				int read = reader.read(buf);
				if (read <= 0)
					break;
				result.append(buf, 0, read);
			}
			return result.toString();
		} finally {
			in.close();
		}
	}

	protected static String removeExtension(final String fileName) {
		return new Path(fileName).removeFileExtension().lastSegment();
	}

}
