package com.yoursway.autoupdater.internal;

import com.yoursway.autoupdater.auxiliary.ProductVersion;

public class ProductVersionStateWrap implements ProductVersionState {
    
    private ProductVersionState state;
    private final ProductVersion version;
    
    public ProductVersionStateWrap(ProductVersion version2) {
        state = new ProductVersionState_New(this);
        this.version = version2;
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
    
}
