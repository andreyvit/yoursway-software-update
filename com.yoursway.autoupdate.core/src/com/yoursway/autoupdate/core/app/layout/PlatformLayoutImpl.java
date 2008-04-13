/**
 * 
 */
package com.yoursway.autoupdate.core.app.layout;

import java.io.File;
import java.io.IOException;

import com.yoursway.utils.YsFileUtils;

public abstract class PlatformLayoutImpl implements PlatformLayout {
    
    private final File pluginsFolder;
    
    public PlatformLayoutImpl(File pluginsFolder) {
        if (!pluginsFolder.isDirectory())
            throw new IllegalArgumentException("Not a directory: " + pluginsFolder);
        this.pluginsFolder = pluginsFolder;
    }
    
    public void createRunnableUpdaterAt(File destinationDir) {
        try {
            putBundleJar("com.yoursway.autoupdate.core.updater", new File(destinationDir, "updater.jar"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void putBundleJar(String bundleName, File destinationFile) throws IOException {
        File jar = YsFileUtils.findEclipsePluginJar(pluginsFolder, bundleName);
        if (jar == null)
            throw new IllegalArgumentException("Cannot find JAR for " + bundleName + " in "
                    + pluginsFolder);
        YsFileUtils.fileCopy(jar, destinationFile);
    }
    
    public File resolvePluginJar(String bundleName) {
        return YsFileUtils.findEclipsePluginJar(pluginsFolder, bundleName);
    }
    
    public Process launch() throws IOException {
        File executable = getExecutable();
        return Runtime.getRuntime().exec(new String[] {executable.toString()});
    }
    
    protected abstract File getExecutable();
    
}