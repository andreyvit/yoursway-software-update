package com.yoursway.autoupdater.auxiliary;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;

import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductDefinitionMemento;

public class ProductDefinition {
    
    private String name;
    private final Collection<ProductVersionDefinition> versions = newHashSet();
    
    public ProductDefinition(String name) {
        this.name = name;
    }
    
    private ProductDefinition() {
        //?
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public static ProductDefinition fromMemento(ProductDefinitionMemento memento) {
        return new ProductDefinition();
    }
    
    public ProductDefinitionMemento toMemento() {
        return ProductDefinitionMemento.newBuilder().build();
    }
    
    public String name() {
        return name;
    }
    
    public void addVersion(ProductVersionDefinition version) {
        versions.add(version);
    }
    
    public Collection<ProductVersionDefinition> versions() {
        return versions;
    }
    
}
