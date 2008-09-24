package com.yoursway.autoupdater.core.localrepository.internal;

import com.yoursway.autoupdater.core.protos.LocalRepositoryProtos.LocalProductVersionMemento.State;

final class ProductVersionState_Idle extends AbstractProductVersionState implements ProductVersionState {
    
    ProductVersionState_Idle(LocalProductVersion version) {
        super(version);
    }
    
    @Override
    public void startUpdating() {
        //>
        throw new UnsupportedOperationException();
    }
    
    public State toMementoState() {
        return State.Idle;
    }
    
}
