package com.yoursway.autoupdater.installer.gui;

public interface InstallerView {
    
    void debug(String msg);
    
    void error(String msg);
    
    void error(Throwable e);
    
    void cycle();
    
    void done();
    
}
