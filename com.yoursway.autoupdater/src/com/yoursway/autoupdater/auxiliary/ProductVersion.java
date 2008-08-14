package com.yoursway.autoupdater.auxiliary;

import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionMemento;

public class ProductVersion {
    
    public Packs packs() {
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
