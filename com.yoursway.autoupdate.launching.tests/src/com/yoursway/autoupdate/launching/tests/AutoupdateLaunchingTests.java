package com.yoursway.autoupdate.launching.tests;

import java.io.InputStream;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Assert;
import org.junit.Test;

import com.yoursway.autoupdate.launching.EclipseApplicationLauncher;
import com.yoursway.autoupdate.launching.ILaunch;

public class AutoupdateLaunchingTests extends Assert {

	private static class TestLaunch implements ILaunch {

		private Process process;

		public void setProcess(Process process) {
			this.process = process;
		}

		public Process process() {
			return process;
		}

	}

	@Test
	public void shouldLaunchAnEclipseApplication() throws Exception {
		TestLaunch launch = new TestLaunch();
		EclipseApplicationLauncher launcher = new EclipseApplicationLauncher(
				"com.yoursway.autoupdate.updater.application");
		launcher.launch(launch, new NullProgressMonitor());
		InputStream inputStream = launch.process().getInputStream();
		int read = 0;
		while (true) {
			int c = inputStream.read();
			if (c == -1)
				break;
			read++;
			System.out.print(c);
		}
		assertTrue(read > 0);
	}

}
