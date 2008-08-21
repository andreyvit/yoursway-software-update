package com.yoursway.autoupdater.auxiliary;

import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductMemento;

public class Product {
    
    public static Product fromMemento(ProductMemento product) {
        return new Product();
    }
    
    public ProductMemento toMemento() {
        return ProductMemento.newBuilder().build();
    }
    
}
