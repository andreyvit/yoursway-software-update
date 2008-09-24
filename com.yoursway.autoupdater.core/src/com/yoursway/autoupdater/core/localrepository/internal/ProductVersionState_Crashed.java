package com.yoursway.autoupdater.core.localrepository.internal;

import com.yoursway.autoupdater.core.protos.LocalRepositoryProtos.LocalProductVersionMemento.State;

public class ProductVersionState_Crashed extends AbstractProductVersionState implements ProductVersionState {
    
    public ProductVersionState_Crashed(LocalProductVersion version) {
        super(version);
    }
    
    public State toMementoState() {
        return State.Crashed;
    }
    
}
