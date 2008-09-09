package com.yoursway.autoupdater.auxiliary;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.Map;

import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductDefinitionMemento;

public class ProductDefinition {
    
    private final String name;
    private final Map<String, ProductVersionDefinition> versions = newHashMap();
    
    public ProductDefinition(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public static ProductDefinition fromMemento(ProductDefinitionMemento memento) {
        return new ProductDefinition(memento.getName());
    }
    
    public ProductDefinitionMemento toMemento() {
        return ProductDefinitionMemento.newBuilder().setName(name).build();
    }
    
    public String name() {
        return name;
    }
    
    public void addVersion(ProductVersionDefinition version) {
        if (versions.containsKey(version.name()))
            throw new IllegalArgumentException("A product version with the name has been added already");
        versions.put(version.name(), version);
    }
    
    public Collection<ProductVersionDefinition> versions() {
        return versions.values();
    }
    
}
