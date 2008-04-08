/**
 * 
 */
package com.yoursway.autoupdate.core.app.layout;

import java.io.File;

import com.yoursway.autoupdate.core.FileContainer;

public interface PlatformLayout {
    
    void createRunnableUpdaterAt(File root);

    FileContainer createFileContainer();

    File resolvePluginJar(String bundleName);
    
}