package com.yoursway.autoupdate.core.tests.mocks;

import static com.google.common.collect.Maps.uniqueIndex;

import java.util.Collection;
import java.util.Map;

import com.yoursway.autoupdate.core.FileContainer;
import com.yoursway.autoupdate.core.fileset.FileSet;
import com.yoursway.autoupdate.core.path.Path;
import com.yoursway.autoupdate.core.versiondef.AppFile;

public class MockFileContainer implements FileContainer {
    
    private final Map<Path, AppFile> files;
    
    public MockFileContainer(Collection<? extends AppFile> files) {
        this.files = uniqueIndex(files, AppFile.APPFILE_TO_PATH);
    }
    
    public FileSet allFiles() {
        return new FileSet(files.keySet());
    }
    
    public AppFile resolve(Path path) {
        return files.get(path);
    }
    
}
