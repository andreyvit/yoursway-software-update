package com.yoursway.autoupdater.localrepository.internal;

import static com.google.common.collect.Lists.newLinkedList;

import java.util.Collection;

import com.yoursway.autoupdater.auxiliary.ProductVersionDefinition;
import com.yoursway.autoupdater.filelibrary.LibraryState;
import com.yoursway.autoupdater.filelibrary.OrderManager;
import com.yoursway.autoupdater.filelibrary.Request;
import com.yoursway.autoupdater.installer.Installer;
import com.yoursway.autoupdater.localrepository.UpdatingListener;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.LocalProductVersionMemento.State;

abstract class AbstractProductVersionState implements ProductVersionState {
    
    protected final LocalProductVersion version;
    
    public AbstractProductVersionState(LocalProductVersion version) {
        this.version = version;
    }
    
    public static ProductVersionState from(State s, LocalProductVersion v) {
        if (s == State.Installing)
            return new ProductVersionState_Installing(v);
        if (s == State.Idle)
            return new ProductVersionState_Idle(v);
        throw new IllegalArgumentException("State s == " + s.toString());
    }
    
    protected final void changeState(ProductVersionState newState) {
        version.changeState(newState);
    }
    
    protected ProductVersionDefinition versionDefinition() {
        return version.definition;
    }
    
    protected LocalProduct product() {
        return version.product;
    }
    
    protected Installer installer() {
        return version.product.installer;
    }
    
    protected OrderManager orderManager() {
        return version.product.orderManager;
    }
    
    protected UpdatingListener listener() {
        return version.listener;
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
    
    public Collection<Request> libraryRequests() {
        return newLinkedList();
    }
    
    public void libraryChanged(LibraryState state) {
        // nothing to do
    }
    
}
