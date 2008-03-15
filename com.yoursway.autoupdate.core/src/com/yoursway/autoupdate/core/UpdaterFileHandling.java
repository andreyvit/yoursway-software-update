package com.yoursway.autoupdate.core;

import com.yoursway.autoupdate.core.filespec.CompoundFileSetSpec;
import com.yoursway.autoupdate.core.filespec.FileSetSpec;

public enum UpdaterFileHandling {
	
	NO_UPDATE {
		
		public void schedule(UpdatePlan plan, UpdaterConfiguration config, CompoundFileSetSpec excludedFiles) {
			plan.useUpdaterAsIs();
		}
		
	},

	REPLACE_INPLACE {
		
		public void schedule(UpdatePlan plan, UpdaterConfiguration config, CompoundFileSetSpec excludedFiles) {
			FileSetSpec files = config.updaterFiles();
			plan.scheduleInplaceUpdaterUpdate(files);
			excludedFiles.add(files);
		}
		
	},
	
	MAKE_COPY {
		
		public void schedule(UpdatePlan plan, UpdaterConfiguration config, CompoundFileSetSpec excludedFiles) {
			plan.scheduleUpdaterCopy(config.updaterFiles());
		}
		
	};
	
	public UpdaterFileHandling worst(UpdaterFileHandling another) {
		if (compareTo(another) > 0)
			return this;
		else
			return another;
	}
	
	public abstract void schedule(UpdatePlan plan, UpdaterConfiguration config, CompoundFileSetSpec excludedFiles);

	
}
