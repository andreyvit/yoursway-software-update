package com.yoursway.autoupdate.core;

import java.util.Collection;

import com.yoursway.utils.filespec.AllFilesSpec;
import com.yoursway.utils.filespec.CompoundFileSetSpec;
import com.yoursway.utils.filespec.ExcludedFileSpec;
import com.yoursway.utils.filespec.FileSetSpec;
import com.yoursway.utils.relativepath.RelativePath;

public class UpdatePlanBuilder {

	private ReplaceStrategy overallReplaceStrategy = ReplaceStrategy.HOT_REPLACE;

	private UpdaterFileHandling updaterFileHandling = UpdaterFileHandling.NO_UPDATE;

	private final Collection<RelativePath> modifiedFiles;

    private final FileSetSpec updaterFiles;

    private final ReplaceTester replaceTester;

	public UpdatePlanBuilder(ReplaceTester replaceTester,
			Collection<RelativePath> modifiedFiles,
			FileSetSpec updaterFiles) {
		this.replaceTester = replaceTester;
        this.modifiedFiles = modifiedFiles;
        this.updaterFiles = updaterFiles;
	}

	public UpdatePlan build() {
		for (RelativePath file : modifiedFiles)
			process(file);
		UpdatePlan plan = new UpdatePlan();
		if (modifiedFiles.isEmpty())
			return plan;
		if (overallReplaceStrategy.needsUpdater()) {
			CompoundFileSetSpec excludedFiles = new CompoundFileSetSpec();
			updaterFileHandling.schedule(plan, updaterFiles, excludedFiles);
			plan.scheduleUpdaterInvokation(new ExcludedFileSpec(
					new AllFilesSpec(), excludedFiles));
		} else {
			plan.scheduleInPlaceUpdate(new AllFilesSpec());
		}
		return plan;
	}

	private void process(RelativePath file) {
		ReplaceStrategy rs = replaceTester.replaceStrategy(file);
		overallReplaceStrategy = overallReplaceStrategy.worst(rs);
		if (updaterFiles.contains(file))
			updaterFileHandling = updaterFileHandling.worst(rs
					.updaterHandlingStrategy());
	}

}
