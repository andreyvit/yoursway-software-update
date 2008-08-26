package com.yoursway.autoupdater.auxiliary;

import static com.google.common.collect.Lists.newLinkedList;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import com.yoursway.autoupdater.filelibrary.Request;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ComponentMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.RequestMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionMemento.Builder;

public class ProductVersion {
    
    private static final String PACKS_PATH = "packs/";
    private final Product product;
    private Collection<Request> packs;
    private final Collection<Component> components;
    private final String status;
    private final String name;
    
    public ProductVersion(Product product, Collection<Request> packs, Collection<Component> components) {
        if (product == null)
            throw new NullPointerException("product is null");
        if (packs == null)
            throw new NullPointerException("packs is null");
        if (components == null)
            throw new NullPointerException("components is null");
        
        this.product = product;
        product.addVersion(this);
        
        this.packs = packs;
        this.components = components;
        
        status = "";
        name = "";
    }
    
    public ProductVersion(Product product, String status, String name, URL updateSite) {
        this.product = product;
        product.addVersion(this);
        
        packs = null;
        components = newLinkedList();
        
        this.status = status;
        this.name = name;
    }
    
    @Override
    public String toString() {
        return product + "-" + name + "-" + status;
    }
    
    public String name() {
        return name;
    }
    
    public void addComponentVersion(Component component) {
        if (packs != null)
            throw new IllegalStateException(
                    "A product version must not add components after collecting its packs.");
        
        components.add(component);
    }
    
    public Product product() {
        return product;
    }
    
    public Collection<Request> packs() {
        if (packs == null) {
            packs = newLinkedList();
            for (Component component : components)
                packs.addAll(component.packs());
        }
        
        return packs;
    }
    
    public Collection<Component> components() {
        return components;
    }
    
    public static ProductVersion fromMemento(ProductVersionMemento memento) throws MalformedURLException {
        Product product = Product.fromMemento(memento.getProduct());
        Collection<Request> packs = newLinkedList();
        for (RequestMemento m : memento.getPackList())
            packs.add(Request.fromMemento(m));
        Collection<Component> components = newLinkedList();
        for (ComponentMemento m : memento.getComponentList())
            components.add(Component.fromMemento(m));
        return new ProductVersion(product, packs, components);
    }
    
    public ProductVersionMemento toMemento() {
        Builder b = ProductVersionMemento.newBuilder().setProduct(product.toMemento());
        for (Request r : packs)
            b.addPack(r.toMemento());
        for (Component c : components)
            b.addComponent(c.toMemento());
        return b.build();
    }
    
    public void execute() {
        //> throw new UnsupportedOperationException();
    }
    
}
