package com.yoursway.autoupdater.localrepository.internal;

import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento.State;

public class ProductVersionState_Current extends AbstractProductVersionState implements ProductVersionState {
    
    public ProductVersionState_Current(ProductVersionStateWrap wrap) {
        super(wrap);
    }
    
    public ProductVersionStateMemento toMemento() {
        return ProductVersionStateMemento.newBuilder().setState(State.Current).setVersion(
                version().toMemento()).build();
    }
    
    @Override
    public boolean isCurrent() {
        return true;
    }
    
}
