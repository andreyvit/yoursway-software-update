/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.yoursway.autoupdate.launching;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;

/**
 * Abstract implementation of a VM runner.
 * <p>
 * Clients implementing VM runners should subclass this class.
 * </p>
 * @see IVMRunner
 * @since 2.0
 */
public abstract class AbstractVMRunner implements IVMRunner {

	/**
	 * Throws a core exception with an error status object built from
	 * the given message, lower level exception, and error code.
	 * 
	 * @param message the status message
	 * @param exception lower level exception associated with the
	 *  error, or <code>null</code> if none
	 * @param code error code
	 * @throws CoreException The exception encapsulating the reason for the abort
	 */
	protected void abort(String message, Throwable exception, int code) throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, getPluginIdentifier(), code, message, exception));
	}
	
	/**
	 * Returns the identifier of the plug-in this VM runner 
	 * originated from.
	 * 
	 * @return plug-in identifier
	 */
	protected abstract String getPluginIdentifier();
	
	/**
	 * @see DebugPlugin#exec(String[], File)
	 */
	protected Process exec(String[] cmdLine, File workingDirectory) throws CoreException {
		return DebugPlugin.exec(cmdLine, workingDirectory);
	}
	
	/**
	 * @since 3.0
	 * @see DebugPlugin#exec(String[], File, String[])
	 */
	protected Process exec(String[] cmdLine, File workingDirectory, String[] envp) throws CoreException {
		return DebugPlugin.exec(cmdLine, workingDirectory, envp);
	}	
	
	/**
	 * Returns the given array of strings as a single space-delimited string.
	 * 
	 * @param cmdLine array of strings
	 * @return a single space-delimited string
	 */
	protected String getCmdLineAsString(String[] cmdLine) {
		StringBuffer buff= new StringBuffer();
		for (int i = 0, numStrings= cmdLine.length; i < numStrings; i++) {
			buff.append(cmdLine[i]);
			buff.append(' ');	
		} 
		return buff.toString().trim();
	}
	
	/**
	 * Returns the default process attribute map for Java processes.
	 * 
	 * @return default process attribute map for Java processes
	 */
	protected Map getDefaultProcessMap() {
		Map map = new HashMap();
		map.put(IProcess.ATTR_PROCESS_TYPE, IJavaLaunchConfigurationConstants.ID_JAVA_PROCESS_TYPE);
		return map;
	}
	
	/**
	 * Returns a new process aborting if the process could not be created.
	 * @param launch the launch the process is contained in
	 * @param p the system process to wrap
	 * @param label the label assigned to the process
	 * @param attributes values for the attribute map
	 * @return the new process
	 * @throws CoreException problems occurred creating the process
	 * @since 3.0
	 */
	protected IProcess newProcess(ILaunch launch, Process p, String label, Map attributes) throws CoreException {
		IProcess process= DebugPlugin.newProcess(launch, p, label, attributes);
		if (process == null) {
			p.destroy();
			abort("Failed to create IProcess", null, IJavaLaunchConfigurationConstants.ERR_INTERNAL_ERROR); 
		}
		return process;
	}
	
	/**
	 * Combines and returns VM arguments specified by the runner configuration,
	 * with those specified by the VM install, if any.
	 * 
	 * @param configuration runner configuration
	 * @param vmInstall vm install
	 * @return combined VM arguments specified by the runner configuration
	 *  and VM install
	 * @since 3.0
	 */
	protected String[] combineVmArgs(VMRunnerConfiguration configuration, String[] vmArgs) {
		String[] launchVMArgs= configuration.getVMArguments();
		String[] vmVMArgs = vmArgs;
		if (vmVMArgs == null || vmVMArgs.length == 0) {
			return launchVMArgs;
		}
		String[] allVMArgs = new String[launchVMArgs.length + vmVMArgs.length];
		System.arraycopy(launchVMArgs, 0, allVMArgs, 0, launchVMArgs.length);
		System.arraycopy(vmVMArgs, 0, allVMArgs, launchVMArgs.length, vmVMArgs.length);
		return allVMArgs;
	}
}
