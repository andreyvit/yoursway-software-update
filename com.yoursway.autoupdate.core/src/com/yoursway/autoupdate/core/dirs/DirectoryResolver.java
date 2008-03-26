package com.yoursway.autoupdate.core.dirs;

import java.io.File;

public interface DirectoryResolver {
    
    File resolveApplicationDirectory();
    
    File resolveTemporaryDirectory();
    
}
