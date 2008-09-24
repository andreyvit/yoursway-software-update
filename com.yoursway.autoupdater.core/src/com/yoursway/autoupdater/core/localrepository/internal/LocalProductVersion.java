package com.yoursway.autoupdater.core.localrepository.internal;

import static com.yoursway.autoupdater.core.protos.LocalRepositoryProtos.LocalProductVersionMemento.newBuilder;
import static com.yoursway.utils.broadcaster.BroadcasterFactory.newBroadcaster;

import java.net.MalformedURLException;
import java.util.Collection;

import com.yoursway.autoupdater.core.auxiliary.AutoupdaterException;
import com.yoursway.autoupdater.core.auxiliary.ErrorsAggregator;
import com.yoursway.autoupdater.core.auxiliary.ProductVersionDefinition;
import com.yoursway.autoupdater.core.filelibrary.FileLibraryListener;
import com.yoursway.autoupdater.core.filelibrary.LibraryState;
import com.yoursway.autoupdater.core.filelibrary.LibrarySubscriber;
import com.yoursway.autoupdater.core.filelibrary.Request;
import com.yoursway.autoupdater.core.localrepository.LocalRepositoryChangerCallback;
import com.yoursway.autoupdater.core.localrepository.UpdatingListener;
import com.yoursway.autoupdater.core.protos.LocalRepositoryProtos.LocalProductVersionMemento;
import com.yoursway.utils.EventSource;
import com.yoursway.utils.broadcaster.Broadcaster;

public class LocalProductVersion implements FileLibraryListener, LibrarySubscriber {
    
    private ProductVersionState state;
    private final LocalProduct product;
    final ProductVersionDefinition definition;
    
    final Broadcaster<UpdatingListener> broadcaster = newBroadcaster(UpdatingListener.class);
    private final LocalRepositoryChangerCallback lrcc;
    final ErrorsAggregator errors = new ErrorsAggregator();
    
    LocalProductVersion(LocalProduct product, ProductVersionDefinition definition,
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
    
    void atStartup() throws AutoupdaterException {
        state.atStartup();
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
    
    public LocalProduct product() {
        return product;
    }
    
    public ProductVersionDefinition definition() {
        return definition;
    }
    
    public EventSource<UpdatingListener> events() {
        return broadcaster;
    }
    
    public boolean failed() {
        return state.failed();
    }
    
}
