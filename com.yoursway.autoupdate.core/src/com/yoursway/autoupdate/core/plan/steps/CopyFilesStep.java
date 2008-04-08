package com.yoursway.autoupdate.core.plan.steps;

import java.io.File;
import java.util.List;

import com.yoursway.autoupdate.core.FilePair;
import com.yoursway.autoupdate.core.UpdateRequest;
import com.yoursway.autoupdate.core.actions.Action;
import com.yoursway.autoupdate.core.actions.concrete.CopyFileAction;
import com.yoursway.autoupdate.core.plan.dirs.Directory;
import com.yoursway.utils.filespec.FileSetSpec;

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
