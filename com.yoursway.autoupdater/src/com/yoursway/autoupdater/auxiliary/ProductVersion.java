package com.yoursway.autoupdater.auxiliary;

import java.util.Collection;

import com.yoursway.autoupdater.filelibrary.Request;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionMemento;

public class ProductVersion {
    
    public Collection<Request> packs() {
        throw new UnsupportedOperationException();
    }
    
    public Product product() {
        throw new UnsupportedOperationException();
    }
    
    public static ProductVersion fromMemento(ProductVersionMemento version) {
        throw new UnsupportedOperationException();
    }
    
    public ProductVersionMemento toMemento() {
        throw new UnsupportedOperationException();
    }
    
}
