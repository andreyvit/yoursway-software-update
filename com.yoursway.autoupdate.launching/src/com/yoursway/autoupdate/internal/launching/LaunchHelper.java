package com.yoursway.autoupdate.internal.launching;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class LaunchHelper {

	public static String[] constructClasspath()
			throws CoreException {

		String jarPath = getEquinoxStartupPath("org.eclipse.equinox.launcher");
		if (jarPath == null)
			return null;

		ArrayList<String> entries = new ArrayList<String>();
		entries.add(jarPath);

		return entries.toArray(new String[entries.size()]);
	}

	private static String getEquinoxStartupPath(String packageName)
			throws CoreException {
		Bundle bundle = Platform.getBundle("org.eclipse.equinox.launcher"); //$NON-NLS-1$
		if (bundle != null) {
			try {
				URL url = FileLocator.resolve(bundle.getEntry("/")); //$NON-NLS-1$
				url = FileLocator.toFileURL(url);
				String path = url.getFile();
				if (path.startsWith("file:")) //$NON-NLS-1$
					path = path.substring(5);
				path = new File(path).getAbsolutePath();
				if (path.endsWith("!")) //$NON-NLS-1$
					path = path.substring(0, path.length() - 1);
				return path;
			} catch (IOException e) {
			}
		}
		return null;
	}

}
