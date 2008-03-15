package com.yoursway.autoupdate.core.tests;

import org.junit.Test;

import com.yoursway.autoupdate.core.tests.internal.AbstractVersionDefinitionLoaderTestCase;
import com.yoursway.autoupdate.core.versiondef.Version;
import com.yoursway.autoupdate.core.versiondef.VersionDefinitionNotAvailable;

public class UrlVersionDefinitionLoaderErrorsTests extends AbstractVersionDefinitionLoaderTestCase {

	private static final Version V10 = new Version("1.0.shit");
	private static final Version V11 = new Version("1.1.shit");
	private static final Version V13 = new Version("1.3.shit");
	private static final Version V142 = new Version("1.42.shit");
	
	@Test(expected=VersionDefinitionNotAvailable.class)
	public void wrongLatestUpdate1() throws Exception {
		loader.loadDefinition(V142);
	}
	
//	@Test(expected=UpdateLoopException.class)
//	public void cyclicLatestUpdate1() throws Exception {
//		updater.latestUpdateFor(V10);
//	}
//	
//	@Test(expected=UpdateLoopException.class)
//	public void cyclicLatestUpdate() throws Exception {
//		updater.latestUpdateFor(V11);
//	}

}
