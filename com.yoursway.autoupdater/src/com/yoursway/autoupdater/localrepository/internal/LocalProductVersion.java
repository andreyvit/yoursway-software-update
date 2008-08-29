package com.yoursway.autoupdater.localrepository.internal;

import static com.yoursway.autoupdater.protos.LocalRepositoryProtos.LocalProductVersionMemento.newBuilder;

import java.net.MalformedURLException;
import java.util.Collection;

import com.yoursway.autoupdater.auxiliary.ProductVersionDefinition;
import com.yoursway.autoupdater.filelibrary.FileLibraryListener;
import com.yoursway.autoupdater.filelibrary.LibraryState;
import com.yoursway.autoupdater.filelibrary.LibrarySubscriber;
import com.yoursway.autoupdater.filelibrary.Request;
import com.yoursway.autoupdater.localrepository.UpdatingListener;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.LocalProductVersionMemento;

public class LocalProductVersion implements FileLibraryListener, LibrarySubscriber {
    
    private ProductVersionState state;
    final LocalProduct product;
    final ProductVersionDefinition definition;
    UpdatingListener listener;
    
    LocalProductVersion(LocalProduct productState, ProductVersionDefinition version, UpdatingListener listener) {
        this.product = productState;
        this.definition = version;
        state = new ProductVersionState_Installing(this);
        
        setListener(listener);
    }
    
    private LocalProductVersion(LocalProductVersionMemento memento, LocalProduct productState)
            throws MalformedURLException {
        this.product = productState;
        definition = ProductVersionDefinition.fromMemento(memento.getDefinition());
        state = AbstractProductVersionState.from(memento.getState(), this);
    }
    
    static LocalProductVersion fromMemento(LocalProductVersionMemento memento, LocalProduct product)
            throws MalformedURLException {
        return new LocalProductVersion(memento, product);
    }
    
    LocalProductVersionMemento toMemento() {
        return newBuilder().setDefinition(definition.toMemento()).setState(state.toMementoState()).build();
    }
    
    void changeState(ProductVersionState newState) {
        state = newState;
        continueWork();
    }
    
    void startUpdating() {
        state.startUpdating();
    }
    
    boolean updating() {
        return state.updating();
    }
    
    void continueWork() {
        state.continueWork();
    }
    
    public Collection<Request> requiredFiles() {
        return state.requiredFiles();
    }
    
    public void libraryChanged(LibraryState s) {
        state.libraryChanged(s);
    }
    
    void setListener(UpdatingListener listener) {
        if (listener == null)
            throw new NullPointerException("listener is null");
        this.listener = listener;
    }
    
}
