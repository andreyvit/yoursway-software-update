package com.yoursway.autoupdater.installer.log;

public class ConsoleLog implements InstallerLog {
    public static ConsoleLog inst;
    
    private ConsoleLog() {
        // nothing
    }
    
    public static InstallerLog inst() {
        if (inst == null)
            inst = new ConsoleLog();
        return inst;
    }

    public void debug(String msg) {
        System.out.println(msg);
    }
    
    public void error(String msg) {
        System.err.println(msg);
    }
    
    public void error(Throwable e) {
        e.printStackTrace(System.err);
    }
}
