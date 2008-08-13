package com.yoursway.autoupdater.internal;

import com.yoursway.autoupdater.auxiliary.ProductVersion;

abstract class AbstractProductVersionState implements ProductVersionState {
    
    protected final ProductVersionStateWrap wrap;
    
    public AbstractProductVersionState(ProductVersionStateWrap wrap) {
        this.wrap = wrap;
    }
    
    protected final void changeState(ProductVersionState newState) {
        wrap.changeState(newState);
    }
    
    protected ProductVersion version() {
        return wrap.version();
    }

    public void startUpdating() {
        throw new IllegalStateException();
    }

    public boolean updating() {
        return false;
    }

    public void continueWork() {
        // nothing to do
    }
    
}
