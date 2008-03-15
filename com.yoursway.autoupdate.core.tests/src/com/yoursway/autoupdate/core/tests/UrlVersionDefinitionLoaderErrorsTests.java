package com.yoursway.autoupdate.core.tests;

import org.junit.Test;

import com.yoursway.autoupdate.core.Version;
import com.yoursway.autoupdate.core.IVersionDefinitionLoader;
import com.yoursway.autoupdate.core.UpdateLoopException;
import com.yoursway.autoupdate.core.tests.internal.AbstractVersionDefinitionLoaderTestCase;

public class UrlVersionDefinitionLoaderErrorsTests extends AbstractVersionDefinitionLoaderTestCase {

	private static final Version V10 = new Version("1.0.shit");
	private static final Version V11 = new Version("1.1.shit");
	private static final Version V13 = new Version("1.3.shit");
	
	@Test(expected=RuntimeException.class)
	public void wrongLatestUpdate1() throws Exception {
		IVersionDefinitionLoader updater = updater();
		updater.latestUpdateFor(V13);
	}
	
	
	@Test(expected=UpdateLoopException.class)
	public void cyclicLatestUpdate1() throws Exception {
		IVersionDefinitionLoader updater = updater();
		updater.latestUpdateFor(V10);
	}
	
	@Test(expected=UpdateLoopException.class)
	public void cyclicLatestUpdate() throws Exception {
		IVersionDefinitionLoader updater = updater();
		updater.latestUpdateFor(V11);
	}

}
