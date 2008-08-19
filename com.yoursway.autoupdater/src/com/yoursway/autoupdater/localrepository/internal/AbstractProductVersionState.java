package com.yoursway.autoupdater.localrepository.internal;

import static com.google.common.collect.Lists.newLinkedList;

import java.util.Collection;

import com.yoursway.autoupdater.auxiliary.ProductVersion;
import com.yoursway.autoupdater.filelibrary.LibraryState;
import com.yoursway.autoupdater.filelibrary.OrderManager;
import com.yoursway.autoupdater.filelibrary.Request;
import com.yoursway.autoupdater.installer.Installer;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento.State;

abstract class AbstractProductVersionState implements ProductVersionState {
    
    protected final ProductVersionStateWrap wrap;
    
    public AbstractProductVersionState(ProductVersionStateWrap wrap) {
        this.wrap = wrap;
    }
    
    public static ProductVersionState from(State s, ProductVersionStateWrap w) {
        if (s == State.Installing)
            return new ProductVersionState_Installing(w);
        if (s == State.Old)
            return new ProductVersionState_Old(w);
        throw new IllegalArgumentException("State s == " + s.toString());
    }
    
    protected final void changeState(ProductVersionState newState) {
        wrap.changeState(newState);
    }
    
    protected ProductVersion version() {
        return wrap.version;
    }
    
    protected ProductState productState() {
        return wrap.productState;
    }
    
    protected Installer installer() {
        return wrap.productState.installer;
    }
    
    protected OrderManager orderManager() {
        return wrap.productState.orderManager;
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
    
    public Collection<Request> requiredFiles() {
        return newLinkedList();
    }
    
    public void libraryChanged(LibraryState state) {
        // nothing to do
    }
    
}
