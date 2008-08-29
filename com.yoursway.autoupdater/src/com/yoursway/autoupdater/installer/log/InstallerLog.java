package com.yoursway.autoupdater.installer.log;

public interface InstallerLog {
    
    void debug(String msg);
    
    void error(String msg);
    
    void error(Throwable e);
    
    static InstallerLog NOP = new InstallerLog() {
        public void debug(String msg) {
        }
        
        public void error(String msg) {
        }
        
        public void error(Throwable e) {
        }
    };
    
}
