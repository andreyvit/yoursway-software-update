/**
 * 
 */
package com.yoursway.autoupdate.core.app.layout;

import java.io.File;
import java.io.IOException;

import com.yoursway.autoupdate.core.FileContainer;
import com.yoursway.utils.relativepath.RelativePath;

public interface PlatformLayout {
    
    void createRunnableUpdaterAt(File root);

    FileContainer createFileContainer();

    File resolveOsgiBundle(String bundleName);

    File resolve(RelativePath path);

    Process launch() throws IOException;
    
}