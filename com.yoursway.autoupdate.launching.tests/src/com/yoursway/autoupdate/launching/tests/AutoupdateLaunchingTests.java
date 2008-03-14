package com.yoursway.autoupdate.launching.tests;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

import com.yoursway.autoupdate.launching.EclipseApplicationLauncher;


public class AutoupdateLaunchingTests {

	@Test
	public void shouldLaunchAnEclipseApplication() throws Exception {
		EclipseApplicationLauncher launcher = new EclipseApplicationLauncher("org.eclipse.ui.ide.workbench");
		launcher.launch(new NullProgressMonitor());
	}
	
}
