package com.yoursway.autoupdate.core;

import com.yoursway.utils.relativepath.RelativePath;

public interface ReplaceTester {
    
    ReplaceStrategy replaceStrategy(RelativePath file);
    
}
