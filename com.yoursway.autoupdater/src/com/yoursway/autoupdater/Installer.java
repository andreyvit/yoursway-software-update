package com.yoursway.autoupdater;

import com.yoursway.autoupdater.auxiliary.ProductVersion;

public class Installer {
    
    private final ProductVersion version;
    
    public Installer(ProductVersion version) {
        this.version = version;
    }
    
    public void install() {
        throw new UnsupportedOperationException();
        
        //> unpack and move files
    }
    
    public boolean restartRequired() {
        throw new UnsupportedOperationException();
        
        //> ask components
    }
    
}
