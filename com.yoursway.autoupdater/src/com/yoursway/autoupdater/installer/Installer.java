package com.yoursway.autoupdater.installer;

import com.yoursway.autoupdater.auxiliary.ComponentStopper;

public interface Installer {
    
    void install(Installation installation, ComponentStopper stopper) throws InstallerException;
    
}
