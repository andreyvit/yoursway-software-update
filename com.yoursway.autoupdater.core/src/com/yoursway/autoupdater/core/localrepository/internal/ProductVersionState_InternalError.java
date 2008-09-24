package com.yoursway.autoupdater.core.localrepository.internal;

import com.yoursway.autoupdater.core.protos.LocalRepositoryProtos.LocalProductVersionMemento.State;

public class ProductVersionState_InternalError extends AbstractProductVersionState {
    
    public ProductVersionState_InternalError(LocalProductVersion version) {
        super(version);
    }
    
    public State toMementoState() {
        return State.InternalError;
    }
    
    @Override
    public boolean failed() {
        return true;
    }
    
    @Override
    public void startUpdating() {
        changeState(new ProductVersionState_Installing(version));
    }
    
}
