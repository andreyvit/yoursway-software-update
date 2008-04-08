/**
 * 
 */
package com.yoursway.autoupdate.core.app.layout;

import java.io.File;

import com.yoursway.autoupdate.core.FileContainer;
import com.yoursway.autoupdate.core.LocalFileContainer;

public class MacRegularPlatformLayout extends PlatformLayoutImpl {
    
    private final File appBundle;
    private final File root;
    
    private MacRegularPlatformLayout(File installLocation, File appBundle) {
        super(new File(installLocation, "plugins"));
        this.root = installLocation;
        this.appBundle = appBundle;
    }
    
    public static MacRegularPlatformLayout explore(File installLocation) {
        File appBundle = new File(installLocation, "Eclipse.app");
        if (!appBundle.isDirectory())
            return null;
        return new MacRegularPlatformLayout(installLocation, appBundle);
    }

    public FileContainer createFileContainer() {
        return new LocalFileContainer(root);
    }
    
}