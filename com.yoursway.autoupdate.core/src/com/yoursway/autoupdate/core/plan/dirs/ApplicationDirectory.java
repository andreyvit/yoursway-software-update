package com.yoursway.autoupdate.core.plan.dirs;

import java.io.File;

public class ApplicationDirectory implements Directory {

	@Override
	public String toString() {
		return "AppDir";
	}

    public File resolve(DirectoryResolver resolver) {
        return resolver.resolveApplicationDirectory();
    }

    public boolean isTemporary() {
        return false;
    }

}
