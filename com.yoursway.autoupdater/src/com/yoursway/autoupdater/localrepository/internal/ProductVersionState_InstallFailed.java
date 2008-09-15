package com.yoursway.autoupdater.localrepository.internal;

import com.yoursway.autoupdater.protos.LocalRepositoryProtos.LocalProductVersionMemento.State;

public class ProductVersionState_InstallFailed extends AbstractProductVersionState implements
        ProductVersionState {
    
    public ProductVersionState_InstallFailed(LocalProductVersion version) {
        super(version);
    }
    
    public State toMementoState() {
        return State.InstallFailed;
    }
    
}
