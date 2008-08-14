package com.yoursway.autoupdater.localrepository.internal;

import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento.State;

public class ProductVersionState_Old extends AbstractProductVersionState implements ProductVersionState {
    
    public ProductVersionState_Old(ProductVersionStateWrap wrap) {
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
