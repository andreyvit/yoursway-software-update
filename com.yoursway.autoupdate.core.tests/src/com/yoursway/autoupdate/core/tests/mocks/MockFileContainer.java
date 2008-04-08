package com.yoursway.autoupdate.core.tests.mocks;

import static com.google.common.collect.Maps.uniqueIndex;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import com.yoursway.autoupdate.core.FileContainer;
import com.yoursway.autoupdate.core.versions.definitions.AppFile;
import com.yoursway.utils.fileset.FileSet;
import com.yoursway.utils.relativepath.RelativePath;

public class MockFileContainer implements FileContainer {
    
    private final Map<RelativePath, AppFile> files;
    
    public MockFileContainer(Collection<? extends AppFile> files) {
        this.files = uniqueIndex(files, AppFile.APPFILE_TO_PATH);
    }
    
    public FileSet allFiles() {
        return new FileSet(files.keySet());
    }
    
    public AppFile resolve(RelativePath relativePath) {
        return files.get(relativePath);
    }

    public RelativePath relativePathOf(File file) {
        return null;
    }
    
}
