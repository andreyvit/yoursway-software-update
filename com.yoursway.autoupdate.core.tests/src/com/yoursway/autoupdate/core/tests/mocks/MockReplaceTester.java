package com.yoursway.autoupdate.core.tests.mocks;

import static com.google.common.collect.Sets.newHashSet;
import static com.yoursway.autoupdate.core.ReplaceStrategy.HOT_REPLACE;
import static com.yoursway.autoupdate.core.ReplaceStrategy.REPLACE_AFTER_SHUTDOWN;

import java.util.Collection;

import com.yoursway.autoupdate.core.ReplaceStrategy;
import com.yoursway.autoupdate.core.ReplaceTester;
import com.yoursway.utils.relativepath.RelativePath;

public class MockReplaceTester implements ReplaceTester {
    
    private final Collection<RelativePath> lockedFiles;

    public MockReplaceTester(Collection<RelativePath> lockedFiles) {
        this.lockedFiles = newHashSet(lockedFiles);
    }

    public ReplaceStrategy replaceStrategy(RelativePath file) {
        if (lockedFiles.contains(file))
            return REPLACE_AFTER_SHUTDOWN;
        return HOT_REPLACE;
    }
    
    
    
}
