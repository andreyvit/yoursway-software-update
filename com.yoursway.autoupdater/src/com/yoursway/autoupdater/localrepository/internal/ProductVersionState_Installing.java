package com.yoursway.autoupdater.localrepository.internal;

import java.io.File;
import java.util.Collection;

import com.yoursway.autoupdater.filelibrary.FileLibraryListener;
import com.yoursway.autoupdater.filelibrary.LibraryState;
import com.yoursway.autoupdater.filelibrary.Request;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento.State;

public class ProductVersionState_Installing extends AbstractProductVersionState implements
        FileLibraryListener {
    
    public ProductVersionState_Installing(ProductVersionStateWrap wrap) {
        super(wrap);
    }
    
    @Override
    public void continueWork() {
        orderManager().orderChanged();
    }
    
    @Override
    public Collection<Request> requiredFiles() {
        return version().packs();
    }
    
    @Override
    public void libraryChanged(LibraryState state) {
        if (state.filesReady(version().packs())) {
            Collection<File> localPacks = state.getLocalFiles(version().packs());
            installer().install(localPacks);
        }
    }
    
    @Override
    public boolean updating() {
        return true;
    }
    
    public ProductVersionStateMemento toMemento() {
        return ProductVersionStateMemento.newBuilder().setState(State.Installing).setVersion(
                version().toMemento()).build();
    }
    
}
