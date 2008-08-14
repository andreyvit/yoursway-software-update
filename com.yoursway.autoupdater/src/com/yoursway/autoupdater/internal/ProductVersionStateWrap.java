package com.yoursway.autoupdater.internal;

import com.yoursway.autoupdater.auxiliary.ProductVersion;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento;

public class ProductVersionStateWrap implements ProductVersionState {
    
    private ProductVersionState state;
    private final ProductVersion version;
    
    public ProductVersionStateWrap(ProductVersion version) {
        state = new ProductVersionState_New(this);
        this.version = version;
    }
    
    private ProductVersionStateWrap(ProductVersionStateMemento memento) {
        version = ProductVersion.fromMemento(memento.getVersion());
        state = AbstractProductVersionState.from(memento.getState(), this);
    }
    
    void changeState(ProductVersionState newState) {
        state = newState;
        continueWork();
    }
    
    ProductVersion version() {
        return version;
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
    
    public static ProductVersionStateWrap fromMemento(ProductVersionStateMemento memento) {
        return new ProductVersionStateWrap(memento);
    }
    
    public ProductVersionStateMemento toMemento() {
        return state.toMemento();
    }
    
}
