package com.yoursway.autoupdate.launching;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

import com.yoursway.autoupdate.internal.launching.IVMRunner;
import com.yoursway.autoupdate.internal.launching.LaunchHelper;
import com.yoursway.autoupdate.internal.launching.MacOSXJavaVM;
import com.yoursway.autoupdate.internal.launching.MacOSXVMRunner;
import com.yoursway.autoupdate.internal.launching.StandardJavaVM;
import com.yoursway.autoupdate.internal.launching.StandardVMRunner;
import com.yoursway.autoupdate.internal.launching.VMRunnerConfiguration;

public class EclipseApplicationLauncher {

	protected File fConfigDir = null;
	private String applicationId;

	public EclipseApplicationLauncher(String applicationId) {
		this.applicationId = applicationId;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration,
	 *      java.lang.String, org.eclipse.debug.core.ILaunch,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void launch(ILaunch launch, IProgressMonitor monitor) throws CoreException {
		try {
			fConfigDir = null;
			monitor.beginTask("", 4); //$NON-NLS-1$

			VMRunnerConfiguration runnerConfig = new VMRunnerConfiguration(
					getMainClass(), getClasspath());
			runnerConfig.setVMArguments(new String[0]); //getVMArguments(configuration)
			runnerConfig
					.setProgramArguments(getProgramArguments());
			runnerConfig.setWorkingDirectory("/Users/fourdman/Eclipses/3.4M5/eclipse/Eclipse.app/Contents/MacOS");
			runnerConfig.setEnvironment(System.getenv());
//			runnerConfig
//					.setVMSpecificAttributesMap(getVMSpecificAttributesMap(configuration));

			monitor.worked(1);

			IVMRunner runner = createRunner();
			if (runner != null)
				runner.run(runnerConfig, launch, monitor);
			else
				monitor.setCanceled(true);
			monitor.done();
		} catch (final CoreException e) {
			monitor.setCanceled(true);
			throw e;
		}
	}

	/**
	 * Returns the entries that should appear on boot classpath.
	 * 
	 * @param configuration
	 *            launch configuration
	 * @return the location of startup.jar and 
	 * 		the bootstrap classpath specified by the given launch configuration
	 *        
	 * @exception CoreException
	 *                if unable to find startup.jar
	 */
	public String[] getClasspath() throws CoreException {
		return LaunchHelper.constructClasspath();
	}

	
	/**
	 * Returns the fully-qualified name of the class to launch.
	 * 
	 * @return the fully-qualified name of the class to launch. Must not return
	 *         <code>null</code>.
	 * @since 3.3
	 */
	public String getMainClass() {
		return "org.eclipse.equinox.launcher.Main"; //$NON-NLS-1$
	}

	private IVMRunner createRunner() {
		if (Platform.getOS().equals(Platform.OS_MACOSX))
			return new MacOSXVMRunner(new MacOSXJavaVM());
		return new StandardVMRunner(new StandardJavaVM());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.ui.launcher.AbstractPDELaunchConfiguration#getProgramArguments(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public String[] getProgramArguments()
			throws CoreException {
		ArrayList<Object> programArgs = new ArrayList<Object>();
		// specify the application to launch
		programArgs.add("-application"); //$NON-NLS-1$
		programArgs.add(applicationId);
		programArgs.add("-os"); //$NON-NLS-1$
		programArgs.add(Platform.getOS());
		programArgs.add("-ws"); //$NON-NLS-1$
		programArgs.add(Platform.getWS());
		programArgs.add("-arch"); //$NON-NLS-1$
		programArgs.add(Platform.getOSArch());
		return programArgs.toArray(new String[programArgs.size()]);
	}



}
