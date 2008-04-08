package com.yoursway.autoupdate.core.versions.definitions;

import org.eclipse.core.runtime.Assert;

import com.yoursway.utils.filespec.FileSetSpec;
import com.yoursway.utils.relativepath.RelativePath;

public class UpdaterInfo {
    
    private FileSetSpec files;
    
    private RelativePath mainJar;

    public UpdaterInfo(FileSetSpec files, RelativePath mainJar) {
        Assert.isNotNull(files);
        Assert.isNotNull(mainJar);
        this.files = files;
        this.mainJar = mainJar;
    }
    
    public FileSetSpec files() {
        return files;
    }
    
    public RelativePath mainJar() {
        return mainJar;
    }
    
}
