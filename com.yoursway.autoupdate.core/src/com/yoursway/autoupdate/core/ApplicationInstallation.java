package com.yoursway.autoupdate.core;

import java.io.File;

import com.yoursway.autoupdate.core.app.layout.MacBundlePlatformLayout;
import com.yoursway.autoupdate.core.app.layout.MacRegularPlatformLayout;
import com.yoursway.autoupdate.core.app.layout.PlatformLayout;
import com.yoursway.autoupdate.core.app.layout.WindowsPlatformLayout;

public class ApplicationInstallation {
    
    private final PlatformLayout layout;

    public ApplicationInstallation(File root) {
        layout = determineLayout(root);
    }
    
	public FileContainer getFileContainer() {
		return layout.createFileContainer();
	}
	
	public File resolvePluginJar(String bundleName) {
	    return layout.resolvePluginJar(bundleName);
	}

    public static PlatformLayout determineLayout(File platformLocation) {
        PlatformLayout layout = WindowsPlatformLayout.explore(platformLocation);
        if (layout == null)
            layout = MacRegularPlatformLayout.explore(platformLocation);
        if (layout == null)
            layout = MacBundlePlatformLayout.explore(platformLocation);
        if (layout == null)
            throw new RuntimeException("Cannot determine platform layout of installation at "
                    + platformLocation);
        return layout;
    }

}
