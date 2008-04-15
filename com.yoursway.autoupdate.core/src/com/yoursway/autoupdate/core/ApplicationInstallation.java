package com.yoursway.autoupdate.core;

import static com.yoursway.utils.relativepath.Pathes.relativePath;

import java.io.File;
import java.io.IOException;

import com.yoursway.autoupdate.core.app.layout.MacBundlePlatformLayout;
import com.yoursway.autoupdate.core.app.layout.MacRegularPlatformLayout;
import com.yoursway.autoupdate.core.app.layout.PlatformLayout;
import com.yoursway.autoupdate.core.app.layout.WindowsPlatformLayout;
import com.yoursway.utils.YsFileUtils;
import com.yoursway.utils.relativepath.Pathes;
import com.yoursway.utils.relativepath.RelativePath;

public class ApplicationInstallation {
    
    private final PlatformLayout layout;

    public ApplicationInstallation(File root) {
        layout = determineLayout(root);
    }
    
	public FileContainer getFileContainer() {
		return layout.createFileContainer();
	}
	
	public File resolveOsgiBundle(String bundleName) {
	    return layout.resolveOsgiBundle(bundleName);
	}
	
	public RelativePath resolveOsgiBundleAsPath(String bundleName) {
	    return unresolve(layout.resolveOsgiBundle(bundleName));
	}
	
	public File resolve(RelativePath path) {
	    return layout.resolve(path);
	}
	
	public File root() {
	    return layout.resolve(relativePath(""));
	}
	
	public RelativePath unresolve(File path) {
	    return YsFileUtils.calculateRelativePath(root(), path);
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

    public void launchAndWait() throws IOException {
        Process process = layout.launch();
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
