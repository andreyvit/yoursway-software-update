package com.yoursway.autoupdate.launching;

import java.io.IOException;

public class MacOSXUtil {

	static String[] wrap(Class clazz, String[] cmdLine) {
		
		for (int i= 0; i < cmdLine.length; i++) {
			// test whether we depend on SWT
			if (useSWT(cmdLine[i]))
				return createSWTlauncher(clazz, cmdLine, cmdLine[0]);
		}
		return cmdLine;
	}
	
	/*
	 * Heuristics: returns true if given argument refers to SWT. 
	 */
	private static boolean useSWT(String arg) {
		return arg.indexOf("swt.jar") >= 0 ||	//$NON-NLS-1$
			   arg.indexOf("org.eclipse.swt") >= 0 ||	//$NON-NLS-1$
			   "-ws".equals(arg);	//$NON-NLS-1$
	}
	
	/*
	 * Returns path to executable.
	 */
	static String[] createSWTlauncher(Class clazz, String[] cmdLine, String vmVersion) {
		
		// the following property is defined if Eclipse is started via java_swt
		String java_swt= System.getProperty("org.eclipse.swtlauncher");	//$NON-NLS-1$
		
		if (java_swt == null) {	
			// not started via java_swt -> now we require that the VM supports the "-XstartOnFirstThread" option
			String[] newCmdLine= new String[cmdLine.length+1];
			int argCount= 0;
			newCmdLine[argCount++]= cmdLine[0];
			newCmdLine[argCount++]= "-XstartOnFirstThread"; //$NON-NLS-1$
			for (int i= 1; i < cmdLine.length; i++)
				newCmdLine[argCount++]= cmdLine[i];
			return newCmdLine;
		}
		
		try {
			// copy java_swt to /tmp in order to get the app name right
			Process process= Runtime.getRuntime().exec(new String[] { "/bin/cp", java_swt, "/tmp" }); //$NON-NLS-1$ //$NON-NLS-2$
			process.waitFor();
			java_swt= "/tmp/java_swt"; //$NON-NLS-1$
		} catch (IOException e) {
			// ignore and run java_swt in place
		} catch (InterruptedException e) {
			// ignore and run java_swt in place
		}
		
		String[] newCmdLine= new String[cmdLine.length+1];
		int argCount= 0;
		newCmdLine[argCount++]= java_swt;
		newCmdLine[argCount++]= "-XXvm=" + vmVersion; //$NON-NLS-1$
		for (int i= 1; i < cmdLine.length; i++)
			newCmdLine[argCount++]= cmdLine[i];
		
		return newCmdLine;
	}
}
