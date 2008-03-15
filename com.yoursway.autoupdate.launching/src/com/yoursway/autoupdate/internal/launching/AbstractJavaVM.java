package com.yoursway.autoupdate.internal.launching;

import java.io.File;

public abstract class AbstractJavaVM {

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IVMInstallType#detectInstallLocation()
	 */
	public abstract File detectInstallLocation();
	
	public File getJavaExecutable() {
		File installLocation = detectInstallLocation();
        if (installLocation != null) {
            return StandardJavaVM.findJavaExecutable(installLocation);
        }
        return null;
	}

}