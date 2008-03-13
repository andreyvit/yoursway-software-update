package com.yoursway.autoupdate.core.tests;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.runtime.Path;
import org.junit.Assert;
import org.junit.Test;

import com.yoursway.autoupdate.core.ApplicationFile;
import com.yoursway.autoupdate.core.ApplicationUpdate;
import com.yoursway.autoupdate.core.ApplicationVersion;
import com.yoursway.autoupdate.core.HTTPBasedApplicationUpdater;
import com.yoursway.autoupdate.core.IApplicationUpdater;

public class AutoUpdateTests extends Assert {

	private static final ApplicationVersion V10 = new ApplicationVersion(
			"1.0.shit", "Megashit 1.0");
	private static final ApplicationVersion V11 = new ApplicationVersion(
			"1.1.shit", "Megashit 1.1");
	private static final ApplicationVersion V12 = new ApplicationVersion(
			"1.2.shit", "Megashit 1.2");

	private IApplicationUpdater createUpdater() {
		return new HTTPBasedApplicationUpdater("http://botva") {
			@Override
			protected InputStream contentsFor(URL url) throws IOException {
				String file = new Path(url.getPath()).lastSegment();
				return Activator.openResource("xmls/" + file);				
			}
		};
	}

	@Test
	public void returnsAvailableVersions() throws Exception {
		IApplicationUpdater updater = createUpdater();
		ApplicationVersion[] availableVersions = updater.availableVersions(V10);
		assertNotNull(availableVersions);
		assertEquals(3, availableVersions.length);
		assertEquals("1.0.shit", availableVersions[0].versionString());
		assertEquals("1.2.shit", availableVersions[2].versionString());
		assertEquals("Megashit 1.1", availableVersions[1].displayName());
	}

	@Test
	public void checksForFreshUpdates1() throws Exception {
		IApplicationUpdater updater = createUpdater();
		boolean freshUpdatesAvailable = updater.freshUpdatesAvailable(V10);
		assertTrue(freshUpdatesAvailable);
	}

	@Test
	public void checksForFreshUpdates0() throws Exception {
		IApplicationUpdater updater = createUpdater();
		boolean freshUpdatesAvailable = updater.freshUpdatesAvailable(V12);
		assertFalse(freshUpdatesAvailable);
	}

	@Test
	public void returnsNextUpdateForUpdateble() throws Exception {
		IApplicationUpdater updater = createUpdater();
		ApplicationUpdate update = updater.nextUpdateFor(V11);
		assertNotNull(update);
		ApplicationVersion version = update.version();
		assertNotNull(version);
		assertEquals(V12.versionString(), version.versionString());
		ApplicationFile[] files = update.files();
		assertNotNull(files);
		assertEquals(2, files.length);
	}

	@Test
	public void returnsNullUpdateForTheLatest() throws Exception {
		IApplicationUpdater updater = createUpdater();
		ApplicationUpdate update = updater.nextUpdateFor(V12);
		assertNull(update);
	}

	@Test
	public void returnsLatestUpdate() throws Exception {
		IApplicationUpdater updater = createUpdater();
		ApplicationUpdate update = updater.nextUpdateFor(V10);
		assertNotNull(update);
		ApplicationVersion version = update.version();
		assertNotNull(version);
		assertEquals("1.1.shit", version.versionString());
		ApplicationFile[] files = update.files();
		assertNotNull(files);
		assertEquals(2, files.length);
	}

}
