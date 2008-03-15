/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.yoursway.autoupdate.internal.launching;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

import com.yoursway.autoupdate.launching.ILaunch;

/**
 * A launcher for running Java main classes.
 */
public class StandardVMRunner extends AbstractVMRunner {

	private File installLocation;
	private File javaExecutable;

	/**
	 * Constructor
	 * 
	 * @param vmInstance
	 */
	public StandardVMRunner(AbstractJavaVM javaVM) {
		installLocation = javaVM.detectInstallLocation();
		javaExecutable = javaVM.getJavaExecutable();
	}

	/**
	 * Prepares the command line from the specified array of strings
	 * 
	 * @param commandLine
	 * @return
	 */
	protected static String renderCommandLine(String[] commandLine) {
		if (commandLine.length < 1)
			return ""; //$NON-NLS-1$
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < commandLine.length; i++) {
			buf.append(' ');
			char[] characters = commandLine[i].toCharArray();
			StringBuffer command = new StringBuffer();
			boolean containsSpace = false;
			for (int j = 0; j < characters.length; j++) {
				char character = characters[j];
				if (character == '\"') {
					command.append('\\');
				} else if (character == ' ') {
					containsSpace = true;
				}
				command.append(character);
			}
			if (containsSpace) {
				buf.append('\"');
				buf.append(command.toString());
				buf.append('\"');
			} else {
				buf.append(command.toString());
			}
		}
		return buf.toString();
	}

	/**
	 * Adds the values of args to the given list v
	 * 
	 * @param args
	 * @param v
	 */
	protected void addArguments(String[] args, List v) {
		if (args == null) {
			return;
		}
		for (int i = 0; i < args.length; i++) {
			v.add(args[i]);
		}
	}

	/**
	 * Returns the working directory to use for the launched VM, or
	 * <code>null</code> if the working directory is to be inherited from the
	 * current process.
	 * 
	 * @return the working directory to use
	 * @exception CoreException
	 *                if the working directory specified by the configuration
	 *                does not exist or is not a directory
	 */
	protected File getWorkingDir(VMRunnerConfiguration config)
			throws CoreException {
		String path = config.getWorkingDirectory();
		if (path == null || path.length() == 0) {
			return null;
		}
		File dir = new File(path);
		if (!dir.isDirectory()) {
			abort(
					MessageFormat
							.format(
									"Specified working directory ({0}) doesn't exist or isn't a directory.",
									new String[] { path }),
					null,
					IJavaLaunchConfigurationConstants.ERR_WORKING_DIRECTORY_DOES_NOT_EXIST);
		}
		return dir;
	}

	/**
	 * @see VMRunner#getPluginIdentifier()
	 */
	protected String getPluginIdentifier() {
		return AutoupdateLaunchingPlugin.getUniqueIdentifier();
	}

	/**
	 * Construct and return a String containing the full path of a java
	 * executable command such as 'java' or 'javaw.exe'. If the configuration
	 * specifies an explicit executable, that is used.
	 * 
	 * @return full path to java executable
	 * @exception CoreException
	 *                if unable to locate an executable
	 */
	protected String constructProgramString(VMRunnerConfiguration config)
			throws CoreException {

		// Look for the user-specified java executable command
		String command = javaExecutable.getAbsolutePath();

		if (command != null)
			return command;
//		
//		// Build the path to the java executable. First try 'bin', and if that
//		// doesn't exist, try 'jre/bin'
//		String installLocation = this.installLocation.getAbsolutePath()
//				+ File.separatorChar;
//		File exe = new File(installLocation
//				+ "bin" + File.separatorChar + command); //$NON-NLS-1$ 		
//		if (fileExists(exe)) {
//			return exe.getAbsolutePath();
//		}
//		exe = new File(exe.getAbsolutePath() + ".exe"); //$NON-NLS-1$
//		if (fileExists(exe)) {
//			return exe.getAbsolutePath();
//		}
//		exe = new File(
//				installLocation
//						+ "jre" + File.separatorChar + "bin" + File.separatorChar + command); //$NON-NLS-1$ //$NON-NLS-2$
//		if (fileExists(exe)) {
//			return exe.getAbsolutePath();
//		}
//		exe = new File(exe.getAbsolutePath() + ".exe"); //$NON-NLS-1$
//		if (fileExists(exe)) {
//			return exe.getAbsolutePath();
//		}

		// not found
		abort(MessageFormat.format("Specified executable ({0}) doesn't exist",
				new String[] { command }), null,
				IJavaLaunchConfigurationConstants.ERR_INTERNAL_ERROR);
		return null;
	}

	/**
	 * Convenience method to determine if the specified file exists or not
	 * 
	 * @param file
	 * @return true if the file indeed exists, false otherwise
	 */
	protected boolean fileExists(File file) {
		return file.exists() && file.isFile();
	}

	protected String convertClassPath(String[] cp) {
		int pathCount = 0;
		StringBuffer buf = new StringBuffer();
		if (cp.length == 0) {
			return ""; //$NON-NLS-1$
		}
		for (int i = 0; i < cp.length; i++) {
			if (pathCount > 0) {
				buf.append(File.pathSeparator);
			}
			buf.append(cp[i]);
			pathCount++;
		}
		return buf.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.launching.IVMRunner#run(org.eclipse.jdt.launching.VMRunnerConfiguration,
	 *      org.eclipse.debug.core.ILaunch,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void run(VMRunnerConfiguration config, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {

		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		IProgressMonitor subMonitor = new SubProgressMonitor(monitor, 1);
		subMonitor.beginTask("launching", 1);
		subMonitor.subTask("Constructing command line");

		String program = constructProgramString(config);

		List arguments = new ArrayList();
		arguments.add(program);

		// VM args are the first thing after the java program so that users can
		// specify
		// options like '-client' & '-server' which are required to be the first
		// option
		String[] allVMArgs = combineVmArgs(config, new String[0]);
		addArguments(allVMArgs, arguments);

		addBootClassPathArguments(arguments, config);

		String[] cp = config.getClassPath();
		if (cp.length > 0) {
			arguments.add("-classpath"); //$NON-NLS-1$
			arguments.add(convertClassPath(cp));
		}
		arguments.add(config.getClassToLaunch());

		String[] programArgs = config.getProgramArguments();
		addArguments(programArgs, arguments);

		String[] cmdLine = new String[arguments.size()];
		arguments.toArray(cmdLine);

		String[] envp = prependJREPath(config.getEnvironment());

		subMonitor.worked(1);

		// check for cancellation
		if (monitor.isCanceled()) {
			return;
		}

		subMonitor.subTask("starting vm");
		Process p = null;
		File workingDir = getWorkingDir(config);
		StringBuilder plainCmdLine = new StringBuilder();
		for (String s : cmdLine) {
			plainCmdLine.append(s);
			plainCmdLine.append(" ");
		}
		System.out.println("Command line: " + plainCmdLine.toString());
		System.out.println("Working dir: " + workingDir);
		p = exec(cmdLine, workingDir, envp);
		if (p == null) {
			throw new RuntimeException("Failed to launch application.");
		}

		// check for cancellation
		if (monitor.isCanceled()) {
			p.destroy();
			return;
		}

		// IProcess process = newProcess(launch, p, renderProcessLabel(cmdLine),
		// getDefaultProcessMap());
		// process.setAttribute(IProcess.ATTR_CMDLINE,
		// renderCommandLine(cmdLine));
		launch.setProcess(p);
		subMonitor.worked(1);
		subMonitor.done();
	}

	/**
	 * Prepends the correct java version variable state to the environment path
	 * for Mac VMs
	 * 
	 * @param env
	 *            the current array of environment variables to run with
	 * @param jdkpath
	 *            the path of the current jdk
	 * @since 3.3
	 */
	protected String[] prependJREPath(String[] env) {
		// if (Platform.OS_MACOSX.equals(Platform.getOS())) {
		// if (fVMInstance instanceof IVMInstall2) {
		// IVMInstall2 vm = (IVMInstall2) fVMInstance;
		// String javaVersion = vm.getJavaVersion();
		// if (javaVersion != null) {
		// if (env == null) {
		// Map map = DebugPlugin.getDefault().getLaunchManager()
		// .getNativeEnvironmentCasePreserved();
		// if (map
		// .containsKey(StandardVMDebugger.JAVA_JVM_VERSION)) {
		// String[] env2 = new String[map.size()];
		// Iterator iterator = map.entrySet().iterator();
		// int i = 0;
		// while (iterator.hasNext()) {
		// Entry entry = (Entry) iterator.next();
		// String key = (String) entry.getKey();
		// if (StandardVMDebugger.JAVA_JVM_VERSION
		// .equals(key)) {
		// env2[i] = key + "=" + javaVersion; //$NON-NLS-1$
		// } else {
		// env2[i] = key
		// + "=" + (String) entry.getValue(); //$NON-NLS-1$
		// }
		// i++;
		// }
		// env = env2;
		// }
		// } else {
		// for (int i = 0; i < env.length; i++) {
		// String string = env[i];
		// if (string
		// .startsWith(StandardVMDebugger.JAVA_JVM_VERSION)) {
		// env[i] = StandardVMDebugger.JAVA_JVM_VERSION
		// + "=" + javaVersion; //$NON-NLS-1$
		// break;
		// }
		// }
		// }
		// }
		// }
		// }
		return env;
	}

	/**
	 * Adds arguments to the bootpath
	 * 
	 * @param arguments
	 * @param config
	 */
	protected void addBootClassPathArguments(List arguments,
			VMRunnerConfiguration config) {
		String[] prependBootCP = null;
		String[] bootCP = null;
		String[] appendBootCP = null;
		Map map = config.getVMSpecificAttributesMap();
		if (map != null) {
			prependBootCP = (String[]) map
					.get(IJavaLaunchConfigurationConstants.ATTR_BOOTPATH_PREPEND);
			bootCP = (String[]) map
					.get(IJavaLaunchConfigurationConstants.ATTR_BOOTPATH);
			appendBootCP = (String[]) map
					.get(IJavaLaunchConfigurationConstants.ATTR_BOOTPATH_APPEND);
		}
		if (prependBootCP == null && bootCP == null && appendBootCP == null) {
			// use old single attribute instead of new attributes if not
			// specified
			bootCP = config.getBootClassPath();
		}
		if (prependBootCP != null) {
			arguments
					.add("-Xbootclasspath/p:" + convertClassPath(prependBootCP)); //$NON-NLS-1$
		}
		if (bootCP != null) {
			if (bootCP.length > 0) {
				arguments.add("-Xbootclasspath:" + convertClassPath(bootCP)); //$NON-NLS-1$
			}
		}
		if (appendBootCP != null) {
			arguments
					.add("-Xbootclasspath/a:" + convertClassPath(appendBootCP)); //$NON-NLS-1$
		}
	}

}
