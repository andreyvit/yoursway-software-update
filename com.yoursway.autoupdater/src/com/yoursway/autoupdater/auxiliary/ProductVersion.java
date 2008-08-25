package com.yoursway.autoupdater.auxiliary;

import static com.google.common.collect.Lists.newLinkedList;

import java.net.MalformedURLException;
import java.util.Collection;

import com.yoursway.autoupdater.filelibrary.Request;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ComponentMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.RequestMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionMemento.Builder;

public class ProductVersion {
    
    private final Product product;
    private final Collection<Request> packs;
    private final Collection<Component> components;
    
    public ProductVersion(Product product, Collection<Request> packs, Collection<Component> components) {
        if (product == null)
            throw new NullPointerException("product is null");
        if (packs == null)
            throw new NullPointerException("packs is null");
        if (components == null)
            throw new NullPointerException("components is null");
        
        this.product = product;
        this.packs = packs;
        this.components = components;
    }
    
    public Product product() {
        return product;
    }
    
    public Collection<Request> packs() {
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
