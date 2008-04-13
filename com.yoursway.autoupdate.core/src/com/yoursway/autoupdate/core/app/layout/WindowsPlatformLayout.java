/**
 * 
 */
package com.yoursway.autoupdate.core.app.layout;

import java.io.File;

import com.yoursway.autoupdate.core.FileContainer;
import com.yoursway.autoupdate.core.LocalFileContainer;

public class WindowsPlatformLayout extends PlatformLayoutImpl {
    
    private final File eclipseExe;
    private final File root;
    
    private WindowsPlatformLayout(File installLocation, File eclipseExe) {
        super(new File(installLocation, "plugins"));
        this.root = installLocation;
        this.eclipseExe = eclipseExe;
    }
    
    public static WindowsPlatformLayout explore(File installLocation) {
        File eclipseExe = new File(installLocation, "eclipse.exe");
        if (!eclipseExe.isFile())
            return null;
        return new WindowsPlatformLayout(installLocation, eclipseExe);
    }
    
    public FileContainer createFileContainer() {
        return new LocalFileContainer(root);
    }
    
}
