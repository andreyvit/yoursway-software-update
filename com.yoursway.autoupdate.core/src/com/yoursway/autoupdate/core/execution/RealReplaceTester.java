package com.yoursway.autoupdate.core.execution;

import com.yoursway.autoupdate.core.ReplaceStrategy;
import com.yoursway.autoupdate.core.ReplaceTester;
import com.yoursway.utils.relativepath.RelativePath;

public class RealReplaceTester implements ReplaceTester {
    
    public RealReplaceTester() {
    }

    public ReplaceStrategy replaceStrategy(RelativePath file) {
        return ReplaceStrategy.HOT_REPLACE;
    }
    
}
