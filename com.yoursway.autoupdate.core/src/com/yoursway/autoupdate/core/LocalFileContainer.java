package com.yoursway.autoupdate.core;

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;

import com.yoursway.autoupdate.core.versions.definitions.AppFile;
import com.yoursway.utils.YsDigest;
import com.yoursway.utils.fileset.FileSet;
import com.yoursway.utils.relativepath.Pathes;
import com.yoursway.utils.relativepath.RelativePath;
import com.yoursway.utils.relativepath.RelativePathImpl;

public class LocalFileContainer implements FileContainer {
    
    private final File root;
    
    public LocalFileContainer(File root) {
        this.root = root;
    }
    
    public FileSet allFiles() {
        Collection<RelativePath> result = newArrayList();
        search(root, result, Pathes.relativePath(""));
        return new FileSet(result);
    }
    
    private void search(File root, Collection<RelativePath> result, RelativePath path) {
        if (!root.isDirectory())
            result.add(path);
        else {
            File[] files = root.listFiles();
            if (files != null)
                for (File file : files)
                    search(file, result, path.append(file.getName()));
        }
    }

    public AppFile resolve(RelativePath relativePath) {
        File file = relativePath.toFile(root);
        if (file.isFile()) {
            try {
                FileInputStream stream = new FileInputStream(file);
                String md5 = YsDigest.md5(stream);
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
