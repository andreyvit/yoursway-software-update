package com.yoursway.autoupdater.localrepository.internal;

import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento.State;

public class ProductVersionState_New extends AbstractProductVersionState {
    
    public ProductVersionState_New(ProductVersionStateWrap wrap) {
        super(wrap);
    }
    
    @Override
    public void startUpdating() {
        changeState(new ProductVersionState_Downloading(wrap));
    }
    
    public ProductVersionStateMemento toMemento() {
        return ProductVersionStateMemento.newBuilder().setState(State.New).setVersion(version().toMemento())
                .build();
    }
}
