package com.yoursway.autoupdate.core.tests;

import static com.yoursway.autoupdate.core.FileStateBuilder.buildActions;
import static com.yoursway.autoupdate.core.FileStateBuilder.modifiedFiles;
import static com.yoursway.autoupdate.core.tests.mocks.MockFileFlags.LOCKED;
import static com.yoursway.autoupdate.core.tests.mocks.MockFileFlags.MAIN_UPDATER_JAR;
import static com.yoursway.autoupdate.core.tests.mocks.MockFileFlags.UPDATER;
import static java.util.EnumSet.of;
import static junit.framework.Assert.assertEquals;

import java.io.File;
import java.text.ParseException;
import java.util.Collection;

import org.junit.Test;

import com.yoursway.autoupdate.core.ExecutablePlan;
import com.yoursway.autoupdate.core.FileAction;
import com.yoursway.autoupdate.core.FileContainer;
import com.yoursway.autoupdate.core.ReplaceTester;
import com.yoursway.autoupdate.core.UpdatePlan;
import com.yoursway.autoupdate.core.UpdatePlanBuilder;
import com.yoursway.autoupdate.core.UpdateRequest;
import com.yoursway.autoupdate.core.tests.mocks.MockAppBuilder;
import com.yoursway.autoupdate.core.tests.mocks.MockExecutor;
import com.yoursway.autoupdate.core.versions.Version;
import com.yoursway.autoupdate.core.versions.definitions.RemoteFile;
import com.yoursway.autoupdate.core.versions.definitions.VersionDefinition;
import com.yoursway.autoupdate.core.versions.definitions.VersionDefinitionParser;

public class SemiIntegrationTests {
    
    @Test
    public void fooChanged() throws ParseException {
        MockExecutor executor = new MockExecutor();
        MockAppBuilder builder = new MockAppBuilder();
        builder.file("eclipse.exe").file("jre/java.exe", of(LOCKED, UPDATER)).file("plugins/app.jar",
                of(LOCKED)).file("plugins/updater.jar", of(UPDATER, MAIN_UPDATER_JAR));
        FileContainer original = builder.container();
        ReplaceTester replaceTester = builder.createReplaceTester();
        builder.update("jre/java.exe");
        Collection<RemoteFile> freshFiles = builder.remoteFiles();
        VersionDefinition freshVersion = new VersionDefinition(new Version("1.2"), "R1.2", null,
                "Everything changed", freshFiles,
                VersionDefinitionParser.parseDate("2008-01-18 21:43 +0600"), builder.createUpdaterInfo());
        
        Collection<FileAction> actions = buildActions(original, freshVersion.files());
        
        UpdatePlanBuilder planBuilder = new UpdatePlanBuilder(replaceTester, modifiedFiles(actions)
                .asCollection(), builder.createUpdaterInfo().files());
        UpdatePlan plan = planBuilder.build();
        ExecutablePlan executablePlan = plan.instantiate(new UpdateRequest(new File("/IDE"), original
                .allFiles(), actions, builder.createUpdaterInfo(), executor));
        executablePlan.execute(executor);
        
        assertEquals("COPY /IDE/plugins/updater.jar TO /tmp/dir1/plugins/updater.jar\n"
                + "COPY /IDE/jre/java.exe TO /tmp/dir1/jre/java.exe\n"
                + "RESTART FROM /tmp/dir2, EXEC /tmp/dir2/plugins/updater.jar AND DO:\n"
                + "- COPY /tmp/download/jre/java.exe TO /IDE/jre/java.exe\n"
                + "- START MAIN ECLIPSE (MockEclipseStartInfo) AND DO:\n" + "- - RM -RF /tmp/dir2", executor
                .toString());
    }
    
}
