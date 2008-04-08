package com.yoursway.autoupdate.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.yoursway.autoupdate.core.versions.definitions.AppFile;
import com.yoursway.utils.fileset.FileSet;
import com.yoursway.utils.relativepath.RelativePath;
import com.yoursway.utils.relativepath.RelativePathImpl;

public class LocalFileContainer implements FileContainer {
    
    private final File root;
    
    public LocalFileContainer(File root) {
        this.root = root;
    }
    
    public FileSet allFiles() {
        return null;
    }
    
    public AppFile resolve(RelativePath relativePath) {
        File file = relativePath.toFile(root);
        if (file.isFile()) {
            try {
                FileInputStream stream = new FileInputStream(file);
                String md5 = Digest.md5(stream);
                return new AppFile(relativePath, md5);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public RelativePath relativePathOf(File file) {
        return relativePathWorker(file.getParentFile(), file.getName());
    }
    
    public RelativePath relativePathWorker(File folder, String suffix) {
        if (folder == null)
            return null;
        if (folder.equals(root))
            return RelativePathImpl.fromPortableString(suffix);
        return relativePathWorker(folder.getParentFile(), folder.getName() + "/" + suffix);
    }
    
}
