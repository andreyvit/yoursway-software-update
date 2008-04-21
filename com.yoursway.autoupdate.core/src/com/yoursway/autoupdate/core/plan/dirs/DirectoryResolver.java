package com.yoursway.autoupdate.core.plan.dirs;

import java.io.File;

public interface DirectoryResolver {
    
    File resolveApplicationDirectory();
    
    File resolveTemporaryDirectory(TemporaryDirectory temporaryDirectory);
    
}
