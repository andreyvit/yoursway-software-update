package com.yoursway.autoupdater.localrepository.internal;

import com.yoursway.autoupdater.protos.LocalRepositoryProtos.LocalProductVersionMemento.State;

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
