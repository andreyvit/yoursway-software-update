package com.yoursway.autoupdate.core.tests.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.google.common.collect.Lists;
import com.yoursway.autoupdate.core.versions.Version;
import com.yoursway.autoupdate.core.versions.definitions.IVersionDefinitionLoader;
import com.yoursway.autoupdate.core.versions.definitions.InvalidVersionDefinitionException;
import com.yoursway.autoupdate.core.versions.definitions.VersionDefinition;
import com.yoursway.autoupdate.core.versions.definitions.VersionDefinitionNotAvailable;

public class Testing {

	public static String joinPath(String c1, String c2) {
		IPath path = new Path(c1).append(c2);
		return path.toPortableString();
	}

	public static String joinPath(String c1, String c2, String c3) {
		return joinPath(joinPath(c1, c2), c3);
	}

	public static String readFile(final String fileName) throws IOException {
		InputStream in = Activator.openResource(fileName);
		return Testing.readAndClose(in);
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

	public static String removeExtension(final String fileName) {
		return new Path(fileName).removeFileExtension().lastSegment();
	}

	public static String findNewerVersions(
			IVersionDefinitionLoader loader, Version current) throws VersionDefinitionNotAvailable, InvalidVersionDefinitionException {
		VersionDefinition definition = loader.loadDefinition(current);
		List<Version> result = Lists.newArrayList();
		for (Version next = definition.nextVersion(); next != null; next = definition
				.nextVersion()) {
			definition = loader.loadDefinition(next);
			result.add(next);
		}
		return result.toString();
	}

}
