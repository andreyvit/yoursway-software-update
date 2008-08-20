package com.yoursway.autoupdater.localrepository.internal;

import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento.State;

public class ProductVersionState_Idle extends AbstractProductVersionState implements ProductVersionState {
    
    public ProductVersionState_Idle(ProductVersionStateWrap wrap) {
        super(wrap);
    }
    
    @Override
    public void startUpdating() {
        //>
        throw new UnsupportedOperationException();
    }
    
    public ProductVersionStateMemento toMemento() {
        return ProductVersionStateMemento.newBuilder().setState(State.Old).setVersion(version().toMemento())
                .build();
    }
    
}
