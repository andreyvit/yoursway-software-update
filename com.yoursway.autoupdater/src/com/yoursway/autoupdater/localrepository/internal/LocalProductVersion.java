package com.yoursway.autoupdater.localrepository.internal;

import static com.yoursway.autoupdater.protos.LocalRepositoryProtos.LocalProductVersionMemento.newBuilder;

import java.net.MalformedURLException;
import java.util.Collection;

import com.yoursway.autoupdater.auxiliary.ProductVersionDefinition;
import com.yoursway.autoupdater.filelibrary.FileLibraryListener;
import com.yoursway.autoupdater.filelibrary.LibraryState;
import com.yoursway.autoupdater.filelibrary.LibrarySubscriber;
import com.yoursway.autoupdater.filelibrary.Request;
import com.yoursway.autoupdater.localrepository.LocalRepositoryChangerCallback;
import com.yoursway.autoupdater.localrepository.UpdatingListener;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.LocalProductVersionMemento;

public class LocalProductVersion implements FileLibraryListener, LibrarySubscriber {
    
    private ProductVersionState state;
    private final LocalProduct product;
    final ProductVersionDefinition definition;
    UpdatingListener listener;
    private final LocalRepositoryChangerCallback lrcc;
    
    LocalProductVersion(LocalProduct product, ProductVersionDefinition definition, UpdatingListener listener,
            LocalRepositoryChangerCallback lrcc) {
        
        if (product == null)
            throw new NullPointerException("product is null");
        if (definition == null)
            throw new NullPointerException("definition is null");
        if (lrcc == null)
            throw new NullPointerException("lrcc is null");
        
        this.product = product;
        this.definition = definition;
        this.lrcc = lrcc;
        state = new ProductVersionState_Installing(this);
        
        setListener(listener);
    }
    
    private LocalProductVersion(LocalProductVersionMemento memento, LocalProduct product,
            LocalRepositoryChangerCallback lrcc) throws MalformedURLException {
        
        if (product == null)
            throw new NullPointerException("product is null");
        if (lrcc == null)
            throw new NullPointerException("lrcc is null");
        
        this.product = product;
        this.lrcc = lrcc;
        definition = ProductVersionDefinition.fromMemento(memento.getDefinition());
        state = AbstractProductVersionState.from(memento.getState(), this);
        
        listener = UpdatingListener.NOP;
    }
    
    static LocalProductVersion fromMemento(LocalProductVersionMemento memento, LocalProduct product,
            LocalRepositoryChangerCallback lrcc) throws MalformedURLException {
        return new LocalProductVersion(memento, product, lrcc);
    }
    
    LocalProductVersionMemento toMemento() {
        return newBuilder().setDefinition(definition.toMemento()).setState(state.toMementoState()).build();
    }
    
    void changeState(ProductVersionState newState) {
        state = newState;
        lrcc.localRepositoryChanged();
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
    
    public Collection<Request> libraryRequests() {
        return state.libraryRequests();
    }
    
    public void libraryChanged(LibraryState s) {
        state.libraryChanged(s);
    }
    
    void setListener(UpdatingListener listener) {
        if (listener == null)
            throw new NullPointerException("listener is null");
        this.listener = listener;
    }
    
    public LocalProduct product() {
        return product;
    }
    
    public ProductVersionDefinition definition() {
        return definition;
    }
    
}
