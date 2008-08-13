package com.yoursway.autoupdater.internal;

import com.yoursway.autoupdater.Downloader;
import com.yoursway.autoupdater.Installer;
import com.yoursway.autoupdater.auxiliary.Packs;
import com.yoursway.autoupdater.auxiliary.ProductVersion;

public class ProductVersionState {
    
    private final ProductVersion version;
    
    private boolean updating;
    
    public ProductVersionState(ProductVersion version) {
        this.version = version;
    }
    
    public boolean updating() {
        return updating;
    }
    
    public void startUpdating() {
        updating = true;
    }
    
    public void continueWork() {
        Packs packs = version.packs();
        new Downloader(null).download(packs); //!
        
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
