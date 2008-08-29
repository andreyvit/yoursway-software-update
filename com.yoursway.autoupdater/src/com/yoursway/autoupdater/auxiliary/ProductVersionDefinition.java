package com.yoursway.autoupdater.auxiliary;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Sets.newHashSet;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import com.yoursway.autoupdater.filelibrary.Request;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ComponentDefinitionMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionDefinitionMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.RequestMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionDefinitionMemento.Builder;

public class ProductVersionDefinition {
    
    private final ProductDefinition product;
    private Collection<Request> packs;
    private final Collection<ComponentDefinition> components;
    private final String status;
    private final String name;
    
    private final String executable;
    
    private boolean damaged;
    
    public ProductVersionDefinition(ProductDefinition product, Collection<Request> packs,
            Collection<ComponentDefinition> components, String executable) {
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
        
        this.executable = executable;
    }
    
    public ProductVersionDefinition(ProductDefinition product, String status, String name, URL updateSite) {
        this.product = product;
        product.addVersion(this);
        
        packs = null;
        components = newLinkedList();
        
        this.status = status;
        this.name = name;
        
        //executable = System.getProperty("user.dir") + "/bin com.yoursway.autoupdater.demo.AfterUpdate class"; //!
        executable = System.getProperty("user.dir") + "/afterupdate.jar";
    }
    
    @Override
    public String toString() {
        return product + "-" + name + "-" + status;
    }
    
    public String name() {
        return name;
    }
    
    public void addComponent(ComponentDefinition component) {
        if (packs != null)
            throw new IllegalStateException(
                    "A product version must not add components after collecting its packs.");
        
        components.add(component);
    }
    
    public ProductDefinition product() {
        return product;
    }
    
    public Collection<Request> packRequests() {
        if (packs == null) {
            packs = newLinkedList();
            for (ComponentDefinition component : components)
                packs.addAll(component.packs());
        }
        
        return packs;
    }
    
    public Collection<ComponentDefinition> components() {
        return components;
    }
    
    public static ProductVersionDefinition fromMemento(ProductVersionDefinitionMemento memento)
            throws MalformedURLException {
        ProductDefinition product = ProductDefinition.fromMemento(memento.getProduct());
        Collection<Request> packs = newLinkedList();
        for (RequestMemento m : memento.getPackList())
            packs.add(Request.fromMemento(m));
        Collection<ComponentDefinition> components = newLinkedList();
        for (ComponentDefinitionMemento m : memento.getComponentList())
            components.add(ComponentDefinition.fromMemento(m));
        String executable = memento.getExecutable();
        return new ProductVersionDefinition(product, packs, components, executable);
    }
    
    public ProductVersionDefinitionMemento toMemento() {
        Builder b = ProductVersionDefinitionMemento.newBuilder().setProduct(product.toMemento())
                .setExecutable(executable);
        for (Request r : packs)
            b.addPack(r.toMemento());
        for (ComponentDefinition c : components)
            b.addComponent(c.toMemento());
        return b.build();
    }
    
    public String executable() {
        return executable; //> relative
    }
    
    public Collection<ComponentFile> files() {
        Collection<ComponentFile> files = newHashSet();
        for (ComponentDefinition component : components)
            files.addAll(component.files());
        return files;
    }
    
    void damage() {
        damaged = true;
    }
    
    public boolean damaged() {
        return damaged;
    }
    
}
