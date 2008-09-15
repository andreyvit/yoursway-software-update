package com.yoursway.autoupdater.localrepository.internal;

import static com.yoursway.autoupdater.installer.external.InstallerCommunication.OK;

import com.yoursway.autoupdater.installer.InstallerException;
import com.yoursway.autoupdater.installer.external.ExternalInstaller;
import com.yoursway.autoupdater.installer.external.InstallerCommunication;
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
            String result = ExternalInstaller.afterInstall();
            
            if (result.equals(OK))
                changeState(new ProductVersionState_Idle(version));
            else if (result.equals(InstallerCommunication.INSTALL_FAILED))
                changeState(new ProductVersionState_InstallFailed(version));
            else if (result.equals(InstallerCommunication.CRASHED))
                changeState(new ProductVersionState_Crashed(version));
            
        } catch (InstallerException e) {
            changeState(new ProductVersionState_InternalError(version));
            
            e.printStackTrace(); //!
            errorOccured(new InstallingFailedException(e));
        }
        
        super.atStartup();
    }
}
