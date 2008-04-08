package com.yoursway.autoupdate.core;

import static com.google.common.collect.Iterators.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.yoursway.utils.relativepath.Pathes.relativePath;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import com.yoursway.autoupdate.core.versions.definitions.AppFile;
import com.yoursway.autoupdate.core.versions.definitions.RemoteFile;
import com.yoursway.autoupdate.core.versions.definitions.VersionDefinition;
import com.yoursway.utils.fileset.FileSet;
import com.yoursway.utils.filespec.ConcreteFilesSpec;
import com.yoursway.utils.relativepath.RelativePath;

public class FileStateBuilder {
    
    public static Collection<FileAction> buildActions(FileContainer existingFilesContainer,
            Collection<? extends RemoteFile> targetFiles) {
        Collection<FileAction> actions = newArrayList();
        for (RemoteFile file : targetFiles) {
            AppFile existing = existingFilesContainer.resolve(file.relativePath());
            if (existing == null)
                actions.add(new FileAction.AddAction(file.relativePath(), file));
            else if (!existing.md5().equals(file.md5()))
                actions.add(new FileAction.UpdateAction(file.relativePath(), file));
        }
        Set<RelativePath> existing = newHashSet(existingFilesContainer.allFiles().asCollection());
        existing.removeAll(newHashSet(transform(targetFiles.iterator(), AppFile.APPFILE_TO_PATH)));
        for (RelativePath file : existing)
            actions.add(new FileAction.RemoveAction(file));
        return actions;
    }
    
    public static FileSet modifiedFiles(Collection<FileAction> actions) {
        return new FileSet(newArrayList(transform(actions.iterator(), FileAction.ACTION_TO_PATH)));
    }
    
//    public static void pseudocode(VersionDefinition targetVersionDef) {
//        ApplicationInstallation installation = new ApplicationInstallation();
//        FileContainer existingFilesContainer = installation.getFileContainer();
//        Collection<RemoteFile> targetFiles = targetVersionDef.files();
//        Collection<FileAction> actions = buildActions(existingFilesContainer, targetFiles);
//        UpdaterConfiguration config = new UpdaterConfiguration(new ConcreteFilesSpec(
//                newArrayList(relativePath("updater.jar"))));
//        UpdatePlanBuilder planBuilder = new UpdatePlanBuilder(config, modifiedFiles(actions).asCollection());
//        UpdatePlan plan = planBuilder.build();
//        ExecutablePlan executablePlan = plan.instantiate(new UpdateRequest(installation, actions));
//        executablePlan.execute(new Executor() {
//            
//            public void restartIntoUpdater(File updaterPath, Collection<RealFileAction> actions) {
//            }
//            
//            public void update(Collection<RealFileAction> actions) {
//            }
//            
//            public File createTemporaryDirectory() {
//                return null;
//            }
//            
//        });
//    }
    
}
