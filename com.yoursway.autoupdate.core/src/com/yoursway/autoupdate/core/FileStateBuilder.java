package com.yoursway.autoupdate.core;

import static com.google.common.collect.Lists.newArrayList;
import static com.yoursway.autoupdate.core.path.Pathes.relativePath;
import static java.util.Collections.emptyList;

import java.io.File;
import java.util.Collection;

import com.yoursway.autoupdate.core.filespec.ConcreteFilesSpec;
import com.yoursway.autoupdate.core.path.Path;
import com.yoursway.autoupdate.core.versiondef.AppFile;
import com.yoursway.autoupdate.core.versiondef.RemoteFile;
import com.yoursway.autoupdate.core.versiondef.VersionDefinition;

public class FileStateBuilder {

	public static Collection<FileAction> buildStates(
			FileContainer existingFilesContainer,
			Collection<? extends AppFile> targetFiles) {
		return emptyList(); // TODO
	}

	public static Collection<Path> modifiedFiles(Collection<FileAction> states) {
		return emptyList();
	}

	public static void pseudocode(VersionDefinition targetVersionDef) {
		ApplicationInstallation installation = new ApplicationInstallation();
		FileContainer existingFilesContainer = installation.getFileContainer();
		Collection<RemoteFile> targetFiles = targetVersionDef.files();
		Collection<FileAction> actions = buildStates(existingFilesContainer,
				targetFiles);
		Collection<Path> modifiedFiles = modifiedFiles(actions);
		UpdaterConfiguration config = new UpdaterConfiguration(
				new ConcreteFilesSpec(newArrayList(relativePath("updater.jar"))));
		UpdatePlanBuilder planBuilder = new UpdatePlanBuilder(config,
				modifiedFiles);
		UpdatePlan plan = planBuilder.build();
		ExecutablePlan executablePlan = plan.instantiate(new UpdateRequest(installation, actions));
		executablePlan.execute(new Executor() {

			public void restartIntoUpdater(File updaterPath,
					Collection<RealFileAction> actions) {
			}

			public void update(Collection<RealFileAction> actions) {
			}

			public File createTemporaryDirectory() {
				return null;
			}
			
		});
	}
}
