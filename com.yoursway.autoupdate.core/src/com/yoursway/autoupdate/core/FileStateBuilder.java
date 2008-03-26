package com.yoursway.autoupdate.core;

import static com.google.common.collect.Iterators.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.yoursway.autoupdate.core.path.Pathes.relativePath;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import com.yoursway.autoupdate.core.fileset.FileSet;
import com.yoursway.autoupdate.core.filespec.ConcreteFilesSpec;
import com.yoursway.autoupdate.core.path.Path;
import com.yoursway.autoupdate.core.versiondef.AppFile;
import com.yoursway.autoupdate.core.versiondef.RemoteFile;
import com.yoursway.autoupdate.core.versiondef.VersionDefinition;

public class FileStateBuilder {
    
    public static Collection<FileAction> buildActions(FileContainer existingFilesContainer,
            Collection<? extends RemoteFile> targetFiles) {
        Collection<FileAction> actions = newArrayList();
        for (RemoteFile file : targetFiles) {
            AppFile existing = existingFilesContainer.resolve(file.path());
            if (existing == null)
                actions.add(new FileAction.AddAction(file.path(), file));
            else if (!existing.md5().equals(file.md5()))
                actions.add(new FileAction.UpdateAction(file.path(), file));
        }
        Set<Path> existing = newHashSet(existingFilesContainer.allFiles().asCollection());
        existing.removeAll(newHashSet(transform(targetFiles.iterator(), AppFile.APPFILE_TO_PATH)));
        for (Path file : existing)
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
