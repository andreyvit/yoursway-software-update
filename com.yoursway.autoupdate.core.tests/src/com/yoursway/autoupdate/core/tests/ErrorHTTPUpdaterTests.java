package com.yoursway.autoupdate.core.tests;

import org.junit.Test;

import com.yoursway.autoupdate.core.Version;
import com.yoursway.autoupdate.core.IApplicationUpdater;
import com.yoursway.autoupdate.core.UpdateLoopException;
import com.yoursway.autoupdate.core.tests.internal.AbstractAutoUpdaterTestCase;

public class ErrorHTTPUpdaterTests extends AbstractAutoUpdaterTestCase {

	private static final Version V10 = new Version("1.0.shit");
	private static final Version V11 = new Version("1.1.shit");
	private static final Version V13 = new Version("1.3.shit");
	
	@Test
	public void wrongLatestUpdate1() throws Exception {
		try {
			IApplicationUpdater updater = updater();
			updater.latestUpdateFor(V13);
			throw new AssertionError("Should fail with RuntimeException");
		} catch (RuntimeException e) {
		}
	}
	
	
	@Test
	public void cyclicLatestUpdate1() throws Exception {
		try {
			IApplicationUpdater updater = updater();
			updater.latestUpdateFor(V10);
			throw new AssertionError("Should fail with UpdateLoopException");
		} catch (UpdateLoopException e) {
		}
	}
	
	@Test
	public void cyclicLatestUpdate() throws Exception {
		try {
			IApplicationUpdater updater = updater();
			updater.latestUpdateFor(V11);
			throw new AssertionError("Should fail with UpdateLoopException");
		} catch (UpdateLoopException e) {
		}
	}

}
