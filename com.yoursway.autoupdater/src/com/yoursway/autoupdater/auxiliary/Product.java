package com.yoursway.autoupdater.auxiliary;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;

import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductMemento;

public class Product {
    
    private String name;
    private final Collection<ProductVersion> versions = newHashSet();
    
    public Product(String productName) {
        name = productName;
    }
    
    private Product() {
        //?
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public static Product fromMemento(ProductMemento product) {
        return new Product();
    }
    
    public ProductMemento toMemento() {
        return ProductMemento.newBuilder().build();
    }
    
    public String name() {
        return name;
    }
    
    public void addVersion(ProductVersion version) {
        versions.add(version);
    }
    
    public Collection<ProductVersion> versions() {
        return versions;
    }
    
}
