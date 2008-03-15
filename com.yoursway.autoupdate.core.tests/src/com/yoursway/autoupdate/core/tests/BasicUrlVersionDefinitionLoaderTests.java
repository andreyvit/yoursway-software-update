package com.yoursway.autoupdate.core.tests;

import static com.yoursway.autoupdate.core.tests.internal.Testing.findNewerVersions;

import org.junit.Test;

import com.yoursway.autoupdate.core.Version;
import com.yoursway.autoupdate.core.tests.internal.AbstractVersionDefinitionLoaderTestCase;

public class BasicUrlVersionDefinitionLoaderTests extends
		AbstractVersionDefinitionLoaderTestCase {

	private static final Version V10 = new Version("1.0.shit");
	private static final Version V11 = new Version("1.1.shit");
	private static final Version V12 = new Version("1.2.shit");

	@Test
	public void availableVersions10() throws Exception {
		assertEquals("[1.1.shit, 1.2.shit]", findNewerVersions(loader, V10));
	}

	@Test
	public void availableVersions11() throws Exception {
		assertEquals("[1.2.shit]", findNewerVersions(loader, V11));
	}

	@Test
	public void availableVersions12() throws Exception {
		assertEquals("[]", findNewerVersions(loader, V12));
	}

	@Test
	public void filesParsing10() throws Exception {
		assertEquals("[plugins/a.jar#abc, plugins/b.jar#def]", filesOf(V10));
	}

	@Test
	public void filesParsing11() throws Exception {
		assertEquals("[plugins/a.jar#abc1, plugins/b.jar#def]", filesOf(V11));
	}

	@Test
	public void filesParsing12() throws Exception {
		assertEquals(
				"[features/c.jar#fff2, plugins/a.jar#abc1, plugins/b.jar#def2]",
				filesOf(V12));
	}

}
