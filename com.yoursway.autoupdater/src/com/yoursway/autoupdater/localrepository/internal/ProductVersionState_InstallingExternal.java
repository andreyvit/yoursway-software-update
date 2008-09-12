package com.yoursway.autoupdater.localrepository.internal;

import com.yoursway.autoupdater.installer.InstallerException;
import com.yoursway.autoupdater.installer.external.ExternalInstaller;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.LocalProductVersionMemento.State;

final class ProductVersionState_InstallingExternal extends AbstractProductVersionState {
    
    public ProductVersionState_InstallingExternal(LocalProductVersion version) {
        super(version);
    }
    
    public State toMementoState() {
        return State.InstallingExternal;
    }
    
    @Override
    public boolean updating() {
        return true;
    }
    
    @Override
    public void atStartup() {
        try {
            ExternalInstaller.afterInstall();
            changeState(new ProductVersionState_Idle(version));
        } catch (InstallerException e) {
            e.printStackTrace(); //!
            //> changeState
        }
        
        super.atStartup();
    }
}
