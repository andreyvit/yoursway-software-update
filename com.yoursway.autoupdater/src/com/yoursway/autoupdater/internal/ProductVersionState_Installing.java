package com.yoursway.autoupdater.internal;

import com.yoursway.autoupdater.Installer;

public class ProductVersionState_Installing extends AbstractProductVersionState {
    
    public ProductVersionState_Installing(ProductVersionStateWrap wrap) {
        super(wrap);
    }
    
    @Override
    public void continueWork() {
        Installer installer = new Installer(version());
        
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
        
        //> check if it installed successfully
        
        changeState(new ProductVersionState_Current(wrap));
    }
    
    @Override
    public boolean updating() {
        return true;
    }
    
}
