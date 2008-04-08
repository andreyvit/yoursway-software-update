package com.yoursway.autoupdate.core.plan.dirs;

import java.io.File;

public interface Directory {
    
    File resolve(DirectoryResolver resolver);

    boolean isTemporary();

}
