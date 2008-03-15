package com.yoursway.autoupdate.core.tests;

import org.junit.Test;

import com.yoursway.autoupdate.core.ApplicationFile;
import com.yoursway.autoupdate.core.VersionDefinition;
import com.yoursway.autoupdate.core.Version;
import com.yoursway.autoupdate.core.IVersionDefinitionLoader;
import com.yoursway.autoupdate.core.tests.internal.AbstractVersionDefinitionLoaderTestCase;

public class BasicUrlVersionDefinitionLoaderTests extends AbstractVersionDefinitionLoaderTestCase {

	private static final Version V10 = new Version("1.0.shit");
	private static final Version V11 = new Version("1.1.shit");
	private static final Version V12 = new Version("1.2.shit");
	
	@Test // not implemented by now
	public void availableVersions() throws Exception {
		IVersionDefinitionLoader updater = updater();
		Version[] availableVersions = updater.availableVersions(V10);
		assertNotNull(availableVersions);
		assertEquals(3, availableVersions.length);
		assertEquals("1.0.shit", availableVersions[0].versionString());
		assertEquals("1.1.shit", availableVersions[1].versionString());
		assertEquals("1.2.shit", availableVersions[2].versionString());
	}

	@Test
	public void checksForFreshUpdates1() throws Exception {
		IVersionDefinitionLoader updater = updater();
		boolean freshUpdatesAvailable = updater.newerVersionExists(V10);
		assertTrue(freshUpdatesAvailable);
	}

	@Test
	public void checksForFreshUpdates0() throws Exception {
		IVersionDefinitionLoader updater = updater();
		boolean freshUpdatesAvailable = updater.newerVersionExists(V12);
		assertFalse(freshUpdatesAvailable);
	}

	@Test
	public void returnsNextUpdateForUpdateble() throws Exception {
		IVersionDefinitionLoader updater = updater();
		VersionDefinition update = updater.nextVersionFor(V10);
		assertNotNull(update);
		Version version = update.version();
		assertNotNull(version);
		assertEquals(V11.versionString(), version.versionString());
		ApplicationFile[] files = update.files();
		assertNotNull(files);
		assertEquals(2, files.length);
	}

	@Test
	public void returnsNextUpdateForUpdateble2() throws Exception {
		IVersionDefinitionLoader updater = updater();
		VersionDefinition update = updater.nextVersionFor(V11);
		Version version = update.version();
		assertEquals(V12.versionString(), version.versionString());
		ApplicationFile[] files = update.files();
		assertNotNull(files);
		assertEquals(3, files.length);
	}

	@Test
	public void returnsNullUpdateForTheLatest() throws Exception {
		IVersionDefinitionLoader updater = updater();
		VersionDefinition update = updater.nextVersionFor(V12);
		assertNull(update);
	}

	@Test
	public void latestUpdate() throws Exception {
		IVersionDefinitionLoader updater = updater();
		VersionDefinition update = updater.latestUpdateFor(V10);
		assertNotNull(update);
		Version version = update.version();
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
