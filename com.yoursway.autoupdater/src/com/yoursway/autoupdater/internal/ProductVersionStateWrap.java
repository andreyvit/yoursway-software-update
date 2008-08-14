package com.yoursway.autoupdater.internal;

import com.yoursway.autoupdater.auxiliary.ProductVersion;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento;

public class ProductVersionStateWrap implements ProductVersionState {
    
    private ProductVersionState state;
    private final ProductVersion version;
    private final ProductState productState;
    
    public ProductVersionStateWrap(ProductVersion version, ProductState productState) {
        this.productState = productState;
        this.version = version;
        state = new ProductVersionState_New(this);
    }
    
    private ProductVersionStateWrap(ProductVersionStateMemento memento, ProductState productState) {
        this.productState = productState;
        version = ProductVersion.fromMemento(memento.getVersion());
        state = AbstractProductVersionState.from(memento.getState(), this);
    }
    
    public static ProductVersionStateWrap fromMemento(ProductVersionStateMemento memento,
            ProductState productState) {
        return new ProductVersionStateWrap(memento, productState);
    }
    
    public ProductVersionStateMemento toMemento() {
        return state.toMemento();
    }
    
    void changeState(ProductVersionState newState) {
        state = newState;
        continueWork();
    }
    
    ProductVersion version() {
        return version;
    }
    
    ProductState productState() {
        return productState;
    }

    public void startUpdating() {
        state.startUpdating();
    }
    
    public boolean updating() {
        return state.updating();
    }
    
    public void continueWork() {
        state.continueWork();
    }
    
    public boolean isCurrent() {
        return state.isCurrent();
    }
    
}
