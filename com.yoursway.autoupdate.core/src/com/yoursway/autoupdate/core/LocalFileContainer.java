package com.yoursway.autoupdate.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.yoursway.autoupdate.core.fileset.FileSet;
import com.yoursway.autoupdate.core.path.Path;
import com.yoursway.autoupdate.core.versiondef.AppFile;

public class LocalFileContainer implements FileContainer {
    
    private final File applicationPath;
    
    /**
     * Implementations are encouraged to cache the results of this call.
     */
    public LocalFileContainer(File applicationPath) {
        this.applicationPath = applicationPath;
    }
    
    public FileSet allFiles() {
        return null;
    }
    
    public AppFile resolve(Path path) {
        File file = path.toFile(applicationPath);
        if (file.isFile()) {
            try {
                FileInputStream stream = new FileInputStream(file);
                String md5 = Digest.md5(stream);
                return new AppFile(path, md5);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
}
