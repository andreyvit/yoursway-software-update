package com.yoursway.autoupdater.core.installer;

import com.yoursway.autoupdater.core.auxiliary.ComponentStopper;

public interface Installer {
    
    void install(Installation installation, ComponentStopper stopper) throws InstallerException;
    
}
