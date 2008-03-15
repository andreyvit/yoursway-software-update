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
	
	@Test
	public void wrongLatestUpdate1() throws Exception {
		try {
			IVersionDefinitionLoader updater = updater();
			updater.latestUpdateFor(V13);
			throw new AssertionError("Should fail with RuntimeException");
		} catch (RuntimeException e) {
		}
	}
	
	
	@Test
	public void cyclicLatestUpdate1() throws Exception {
		try {
			IVersionDefinitionLoader updater = updater();
			updater.latestUpdateFor(V10);
			throw new AssertionError("Should fail with UpdateLoopException");
		} catch (UpdateLoopException e) {
		}
	}
	
	@Test
	public void cyclicLatestUpdate() throws Exception {
		try {
			IVersionDefinitionLoader updater = updater();
			updater.latestUpdateFor(V11);
			throw new AssertionError("Should fail with UpdateLoopException");
		} catch (UpdateLoopException e) {
		}
	}

}
