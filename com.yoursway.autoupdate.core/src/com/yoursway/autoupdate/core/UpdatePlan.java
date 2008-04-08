package com.yoursway.autoupdate.core;

import static com.google.common.base.Functions.TO_STRING;
import static com.google.common.base.Join.join;
import static com.google.common.base.Predicates.isNull;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;

import java.util.List;

import com.yoursway.autoupdate.core.actions.Action;
import com.yoursway.autoupdate.core.plan.dirs.ApplicationDirectory;
import com.yoursway.autoupdate.core.plan.dirs.Directory;
import com.yoursway.autoupdate.core.plan.dirs.UpdaterDirectory;
import com.yoursway.autoupdate.core.plan.steps.CopyFilesStep;
import com.yoursway.autoupdate.core.plan.steps.UpdateExternallyStep;
import com.yoursway.autoupdate.core.plan.steps.UpdateFilesStep;
import com.yoursway.autoupdate.core.plan.steps.UpdateStep;
import com.yoursway.utils.filespec.FileSetSpec;

public class UpdatePlan {
	
	private UpdateFilesStep updateUpdaterStep;
	
	private CopyFilesStep copyUpdaterStep;
	
	private UpdateFilesStep updateApplicationFilesStep;
	
	private UpdateExternallyStep updateExternallyStep;

	private Directory updaterLocation;
	
	public List<UpdateStep> steps() {
		List<UpdateStep> result = newArrayList();
		result.add(updateUpdaterStep);
		result.add(copyUpdaterStep);
		result.add(updateApplicationFilesStep);
		result.add(updateExternallyStep);
		return newArrayList(filter(result, not(isNull())));
	}

	@Override
	public String toString() {
		return join("\n", transform(steps(), TO_STRING));
	}

	public void scheduleInplaceUpdaterUpdate(FileSetSpec files) {
		updaterLocation = new ApplicationDirectory();
		updateUpdaterStep = new UpdateFilesStep(files);
	}

	public void scheduleUpdaterCopy(FileSetSpec updaterFiles) {
		updaterLocation = new UpdaterDirectory();
		copyUpdaterStep = new CopyFilesStep(updaterFiles, updaterLocation);
	}
	
	public void scheduleInPlaceUpdate(FileSetSpec files) {
		updateApplicationFilesStep = new UpdateFilesStep(files);
	}

	public void scheduleUpdaterInvokation(FileSetSpec files) {
		updateExternallyStep = new UpdateExternallyStep(updaterLocation, files);
	}

	public void useUpdaterAsIs() {
		updaterLocation = new ApplicationDirectory();
	}

	public ExecutablePlan instantiate(UpdateRequest request) {
	    List<Action> actions = newArrayList();
	    for (UpdateStep step : steps())
	        step.createActions(request, actions);
		return new ExecutablePlan(actions);
	}
	
}
