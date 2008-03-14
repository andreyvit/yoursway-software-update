package com.yoursway.autoupdate.core.tests;

import org.junit.Test;

import com.yoursway.autoupdate.core.ApplicationVersion;
import com.yoursway.autoupdate.core.IApplicationUpdater;
import com.yoursway.autoupdate.core.UpdateLoopException;
import com.yoursway.autoupdate.core.tests.internal.AbstractAutoUpdaterTestCase;

public class ErrorHTTPUpdaterTests extends AbstractAutoUpdaterTestCase {

	private static final ApplicationVersion V10 = new ApplicationVersion(
			"1.0.shit", "Megashit 1.0");
	private static final ApplicationVersion V11 = new ApplicationVersion(
			"1.1.shit", "Megashit 1.1");
	private static final ApplicationVersion V13 = new ApplicationVersion(
			"1.3.shit", "Megashit 1.3");

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
