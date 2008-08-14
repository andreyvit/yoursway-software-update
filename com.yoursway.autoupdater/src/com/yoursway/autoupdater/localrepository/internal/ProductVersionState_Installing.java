package com.yoursway.autoupdater.localrepository.internal;

import com.yoursway.autoupdater.internal.installer.Installer;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento.State;

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
        
        ProductVersionStateWrap current = productState().currentVersionStateOrNull();
        if (current != null)
            current.changeState(new ProductVersionState_Old(current));
        
        changeState(new ProductVersionState_Current(wrap));
    }
    
    @Override
    public boolean updating() {
        return true;
    }
    
    public ProductVersionStateMemento toMemento() {
        return ProductVersionStateMemento.newBuilder().setState(State.Installing).setVersion(
                version().toMemento()).build();
    }
    
}
