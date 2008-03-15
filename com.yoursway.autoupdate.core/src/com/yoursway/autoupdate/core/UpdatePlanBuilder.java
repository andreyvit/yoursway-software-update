package com.yoursway.autoupdate.core;

import java.util.Collection;

import com.yoursway.autoupdate.core.filespec.AllFilesSpec;
import com.yoursway.autoupdate.core.filespec.CompoundFileSetSpec;
import com.yoursway.autoupdate.core.filespec.ExcludedFileSpec;
import com.yoursway.autoupdate.core.path.Path;

public class UpdatePlanBuilder {

	private final UpdaterConfiguration config;

	private ReplaceStrategy overallReplaceStrategy = ReplaceStrategy.HOT_REPLACE;

	private UpdaterFileHandling updaterFileHandling = UpdaterFileHandling.NO_UPDATE;

	private final Collection<Path> modifiedFiles;

	public UpdatePlanBuilder(UpdaterConfiguration config,
			Collection<Path> modifiedFiles) {
		this.config = config;
		this.modifiedFiles = modifiedFiles;
	}

	public UpdatePlan build() {
		for (Path file : modifiedFiles)
			process(file);
		UpdatePlan plan = new UpdatePlan();
		if (modifiedFiles.isEmpty())
			return plan;
		if (overallReplaceStrategy.needsUpdater()) {
			CompoundFileSetSpec excludedFiles = new CompoundFileSetSpec();
			updaterFileHandling.schedule(plan, config, excludedFiles);
			plan.scheduleUpdaterInvokation(new ExcludedFileSpec(
					new AllFilesSpec(), excludedFiles));
		} else {
			plan.scheduleInPlaceUpdate(new AllFilesSpec());
		}
		return plan;
	}

	private void process(Path file) {
		ReplaceStrategy rs = config.replaceStrategy(file);
		overallReplaceStrategy = overallReplaceStrategy.worst(rs);
		if (config.isPartOfUpdater(file))
			updaterFileHandling = updaterFileHandling.worst(rs
					.updaterHandlingStrategy());
	}

}
