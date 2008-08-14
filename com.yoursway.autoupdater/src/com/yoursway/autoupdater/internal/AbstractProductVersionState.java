package com.yoursway.autoupdater.internal;

import com.yoursway.autoupdater.auxiliary.ProductVersion;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento.State;

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
    
    public static ProductVersionState from(State s, ProductVersionStateWrap w) {
        if (s == State.New)
            return new ProductVersionState_New(w);
        if (s == State.Downloading)
            return new ProductVersionState_Downloading(w);
        if (s == State.Installing)
            return new ProductVersionState_Installing(w);
        if (s == State.Current)
            return new ProductVersionState_Current(w);
        if (s == State.Old)
            return new ProductVersionState_Old(w);
        throw new IllegalArgumentException("State s == " + s.toString());
    }
}
