package com.yoursway.autoupdate.core.tests;

import org.junit.Test;

import com.yoursway.autoupdate.core.ApplicationFile;
import com.yoursway.autoupdate.core.VersionDefinition;
import com.yoursway.autoupdate.core.ApplicationVersion;
import com.yoursway.autoupdate.core.IApplicationUpdater;
import com.yoursway.autoupdate.core.tests.internal.AbstractAutoUpdaterTestCase;

public class BasicHTTPUpdaterTests extends AbstractAutoUpdaterTestCase {

	private static final ApplicationVersion V10 = new ApplicationVersion(
			"1.0.shit");
	private static final ApplicationVersion V11 = new ApplicationVersion(
			"1.1.shit");
	private static final ApplicationVersion V12 = new ApplicationVersion(
			"1.2.shit");

	
	@Test // not implemented by now  
	public void availableVersions() throws Exception {
		IApplicationUpdater updater = updater();
		ApplicationVersion[] availableVersions = updater.availableVersions(V10);
		assertNotNull(availableVersions);
		assertEquals(3, availableVersions.length);
		assertEquals("1.0.shit", availableVersions[0].versionString());
		assertEquals("1.1.shit", availableVersions[1].versionString());
		assertEquals("1.2.shit", availableVersions[2].versionString());
	}

	@Test
	public void checksForFreshUpdates1() throws Exception {
		IApplicationUpdater updater = updater();
		boolean freshUpdatesAvailable = updater.freshUpdatesAvailable(V10);
		assertTrue(freshUpdatesAvailable);
	}

	@Test
	public void checksForFreshUpdates0() throws Exception {
		IApplicationUpdater updater = updater();
		boolean freshUpdatesAvailable = updater.freshUpdatesAvailable(V12);
		assertFalse(freshUpdatesAvailable);
	}

	@Test
	public void returnsNextUpdateForUpdateble() throws Exception {
		IApplicationUpdater updater = updater();
		VersionDefinition update = updater.nextUpdateFor(V10);
		assertNotNull(update);
		ApplicationVersion version = update.version();
		assertNotNull(version);
		assertEquals(V11.versionString(), version.versionString());
		ApplicationFile[] files = update.files();
		assertNotNull(files);
		assertEquals(2, files.length);
	}

	@Test
	public void returnsNextUpdateForUpdateble2() throws Exception {
		IApplicationUpdater updater = updater();
		VersionDefinition update = updater.nextUpdateFor(V11);
		ApplicationVersion version = update.version();
		assertEquals(V12.versionString(), version.versionString());
		ApplicationFile[] files = update.files();
		assertNotNull(files);
		assertEquals(3, files.length);
	}

	@Test
	public void returnsNullUpdateForTheLatest() throws Exception {
		IApplicationUpdater updater = updater();
		VersionDefinition update = updater.nextUpdateFor(V12);
		assertNull(update);
	}

	@Test
	public void latestUpdate() throws Exception {
		IApplicationUpdater updater = updater();
		VersionDefinition update = updater.latestUpdateFor(V10);
		assertNotNull(update);
		ApplicationVersion version = update.version();
		assertNotNull(version);
		assertEquals(V12.versionString(), version.versionString());
		ApplicationFile[] files = update.files();
		assertNotNull(files);
		assertEquals(3, files.length);
	}

	@Test
	public void directUpdateToLatest() throws Exception {
		
	}
	
	@Test
	public void directUpdateToPrevious() throws Exception {
		
	}
	
}
