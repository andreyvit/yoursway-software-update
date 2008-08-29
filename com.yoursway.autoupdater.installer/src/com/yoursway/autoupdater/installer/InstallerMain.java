package com.yoursway.autoupdater.installer;

import com.yoursway.autoupdater.installer.gui.ConsoleView;
import com.yoursway.autoupdater.installer.gui.InstallerView;
import com.yoursway.autoupdater.installer.gui.SWTView;

public class InstallerMain {
    
    public static void main(String[] args) {
        boolean gui = args.length > 0 && args[0].equals("gui");
        InstallerView view = gui ? new SWTView() : new ConsoleView();
        
        InstallerThread thread = new InstallerThread(view, view.getLog());
        thread.start();
        
        view.doMessageLoop();
    }
}
