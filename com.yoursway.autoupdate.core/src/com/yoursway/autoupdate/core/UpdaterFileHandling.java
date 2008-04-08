package com.yoursway.autoupdate.core;

import com.yoursway.utils.filespec.CompoundFileSetSpec;
import com.yoursway.utils.filespec.FileSetSpec;

public enum UpdaterFileHandling {
	
	NO_UPDATE {
		
		public void schedule(UpdatePlan plan, FileSetSpec updaterFiles, CompoundFileSetSpec excludedFiles) {
			plan.useUpdaterAsIs();
		}
		
	},

	REPLACE_INPLACE {
		
		public void schedule(UpdatePlan plan, FileSetSpec updaterFiles, CompoundFileSetSpec excludedFiles) {
			plan.scheduleInplaceUpdaterUpdate(updaterFiles);
			excludedFiles.add(updaterFiles);
		}
		
	},
	
	MAKE_COPY {
		
		public void schedule(UpdatePlan plan, FileSetSpec updaterFiles, CompoundFileSetSpec excludedFiles) {
			plan.scheduleUpdaterCopy(updaterFiles);
		}
		
	};
	
	public UpdaterFileHandling worst(UpdaterFileHandling another) {
		if (compareTo(another) > 0)
			return this;
		else
			return another;
	}
	
	public abstract void schedule(UpdatePlan plan, FileSetSpec updaterFiles, CompoundFileSetSpec excludedFiles);

	
}
