/**
 * 
 */
package com.yoursway.autoupdate.core.app.layout;

import java.io.File;

import com.yoursway.autoupdate.core.FileContainer;
import com.yoursway.autoupdate.core.LocalFileContainer;

public class MacBundlePlatformLayout extends PlatformLayoutImpl {
    
    private final File bundleContentsFolder;
    
    private MacBundlePlatformLayout(File installLocation, File bundleContentsFolder) {
        super(new File(installLocation, "plugins"));
        this.bundleContentsFolder = bundleContentsFolder;
    }
    
    public static MacBundlePlatformLayout explore(File installLocation) {
        File parent = installLocation.getParentFile();
        if (parent != null)
            parent = parent.getParentFile();
        if (parent == null || !parent.isDirectory() || !"Contents".equals(parent.getName()))
            return null;
        return new MacBundlePlatformLayout(installLocation, parent);
    }

    public FileContainer createFileContainer() {
        return new LocalFileContainer(bundleContentsFolder);
    }

}