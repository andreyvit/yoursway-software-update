package com.yoursway.autoupdater.auxiliary;

import java.util.Collection;

import com.yoursway.autoupdater.filelibrary.Request;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionMemento;

public class ProductVersion {
    
    private final Product product;
    private final Collection<Request> packs;
    
    public ProductVersion(Product product, Collection<Request> packs) {
        if (product == null)
            throw new NullPointerException("product is null");
        if (packs == null)
            throw new NullPointerException("packs is null");
        
        this.product = product;
        this.packs = packs;
    }
    
    public Collection<Request> packs() {
        return packs;
    }
    
    public Product product() {
        return product;
    }
    
    public static ProductVersion fromMemento(ProductVersionMemento version) {
        throw new UnsupportedOperationException();
    }
    
    public ProductVersionMemento toMemento() {
        return ProductVersionMemento.newBuilder().setName("UNNAMED").build();
    }
}
