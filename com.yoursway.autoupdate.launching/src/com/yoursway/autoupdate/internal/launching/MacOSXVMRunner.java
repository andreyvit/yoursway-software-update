/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
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

import org.eclipse.core.runtime.CoreException;

public class MacOSXVMRunner extends StandardVMRunner {
	
	public MacOSXVMRunner(AbstractJavaVM javaVm) {
		super(javaVm);
	}
	
	protected Process exec(String[] cmdLine, File workingDirectory) throws CoreException {
		return super.exec(MacOSXUtil.wrap(getClass(), cmdLine), workingDirectory);
	}

	protected Process exec(String[] cmdLine, File workingDirectory, String[] envp) throws CoreException {
		return super.exec(MacOSXUtil.wrap(getClass(), cmdLine), workingDirectory, envp);
	}	
}
