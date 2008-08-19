package com.yoursway.autoupdater.installer;

import java.io.File;
import java.util.Collection;

public interface Installer {
    
    public void install(Collection<File> localPacks);
    
}
