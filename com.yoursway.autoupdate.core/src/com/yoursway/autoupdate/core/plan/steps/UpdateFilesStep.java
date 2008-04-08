package com.yoursway.autoupdate.core.plan.steps;

import java.util.List;

import com.yoursway.autoupdate.core.UpdateRequest;
import com.yoursway.autoupdate.core.actions.Action;
import com.yoursway.utils.filespec.FileSetSpec;

public class UpdateFilesStep implements UpdateStep {
    
	private final FileSetSpec fileset;

	public UpdateFilesStep(FileSetSpec fileset) {
		this.fileset = fileset;
	}
	
	@Override
	public String toString() {
		return "UPDATE " + fileset;
	}

    public void createActions(UpdateRequest request, List<Action> storeInto) {
        storeInto.addAll(request.resolveUpdate(fileset));
    }

}
