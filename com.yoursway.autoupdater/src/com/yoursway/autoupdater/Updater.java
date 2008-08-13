package com.yoursway.autoupdater;

import com.yoursway.autoupdater.auxiliary.ProductVersion;

public class Updater {
    
    private Downloader downloader;
    
    public void updateTo(ProductVersion version) {
        downloader.download(version.packs());
        //> check if packs downloaded successfully
        
        Installer installer = new Installer(version);
        
        if (installer.restartRequired()) {
            //> make installation script
            //> run installation script
            //> wait for a signal
            //> quit
        } else {
            //> prepare components
            installer.install();
            //> postpare components
        }
    }
    
}
