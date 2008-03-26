package com.yoursway.autoupdate.core.steps;

import java.io.File;
import java.util.List;

import com.yoursway.autoupdate.core.Action;
import com.yoursway.autoupdate.core.FilePair;
import com.yoursway.autoupdate.core.UpdateRequest;
import com.yoursway.autoupdate.core.actions.CopyFileAction;
import com.yoursway.autoupdate.core.dirs.Directory;
import com.yoursway.autoupdate.core.filespec.FileSetSpec;

public class CopyFilesStep implements UpdateStep {
    
    private final FileSetSpec fileset;
    private final Directory target;
    
    public CopyFilesStep(FileSetSpec fileset, Directory target) {
        this.fileset = fileset;
        this.target = target;
    }
    
    @Override
    public String toString() {
        return "COPY " + fileset + " INTO " + target;
    }
    
    public void createActions(UpdateRequest request, List<Action> storeInto) {
        File t = request.resolve(target);
        for (FilePair pair : request.resolvePairs(fileset, t))
            storeInto.add(new CopyFileAction(pair.source(), pair.destination()));
    }
    
}
