package com.yoursway.autoupdate.core.tests;

import static com.google.common.collect.Lists.newArrayList;
import static com.yoursway.utils.relativepath.Pathes.relativePath;
import static junit.framework.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.yoursway.autoupdate.core.ReplaceStrategy;
import com.yoursway.autoupdate.core.UpdatePlanBuilder;
import com.yoursway.autoupdate.core.UpdaterConfiguration;
import com.yoursway.utils.filespec.ConcreteFilesSpec;
import com.yoursway.utils.relativepath.RelativePath;

public class UpdatePlanBuilderTests {
    
    private static final List<RelativePath> NOTHING_CHANGED = newArrayList();
    
    private static final RelativePath UPDATER_JAR = relativePath("updater.jar");
    
    private static final RelativePath JVM_JAR = relativePath("jvm.jar");
    
    private static final RelativePath FOO_JAR = relativePath("foo.jar");
    
    private static final RelativePath BAR_JAR = relativePath("bar.jar");
    
    private static final UpdaterConfiguration CONFIG = new UpdaterConfiguration(new ConcreteFilesSpec(
            newArrayList(UPDATER_JAR, JVM_JAR)), UPDATER_JAR) {
        
        @Override
        public ReplaceStrategy replaceStrategy(RelativePath file) {
            if (file.equals(FOO_JAR) || file.equals(UPDATER_JAR))
                return ReplaceStrategy.HOT_REPLACE;
            else
                return ReplaceStrategy.REPLACE_AFTER_SHUTDOWN;
        }
        
    };
    
    @Test
    public void nothingChanged() {
        UpdatePlanBuilder pb = new UpdatePlanBuilder(CONFIG, NOTHING_CHANGED);
        assertEquals("", build(pb));
    }
    
    private String build(UpdatePlanBuilder pb) {
        return pb.build().toString();
    }
    
    @Test
    public void fooChanged() {
        UpdatePlanBuilder pb = new UpdatePlanBuilder(CONFIG, newArrayList(FOO_JAR));
        assertEquals("UPDATE *", build(pb));
    }
    
    @Test
    public void barChanged() {
        UpdatePlanBuilder pb = new UpdatePlanBuilder(CONFIG, newArrayList(BAR_JAR));
        assertEquals("RESTART FROM AppDir UPDATE *", build(pb));
    }
    
    @Test
    public void updaterChanged() {
        UpdatePlanBuilder pb = new UpdatePlanBuilder(CONFIG, newArrayList(UPDATER_JAR));
        assertEquals("UPDATE *", build(pb));
    }
    
    @Test
    public void updaterAndBarChanged() {
        UpdatePlanBuilder pb = new UpdatePlanBuilder(CONFIG, newArrayList(UPDATER_JAR, BAR_JAR));
        assertEquals("UPDATE [jvm.jar, updater.jar]\n"
                + "RESTART FROM AppDir UPDATE (* WITHOUT [jvm.jar, updater.jar])", build(pb));
    }
    
    @Test
    public void jvmChanged() {
        UpdatePlanBuilder pb = new UpdatePlanBuilder(CONFIG, newArrayList(JVM_JAR));
        assertEquals("COPY [jvm.jar, updater.jar] INTO UpdaterTempDir\n"
                + "RESTART FROM UpdaterTempDir UPDATE *", build(pb));
    }
    
}
