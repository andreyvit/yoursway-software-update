package com.yoursway.autoupdater.auxiliary;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Sets.newHashSet;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import com.yoursway.autoupdater.filelibrary.Request;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ComponentMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.RequestMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionMemento.Builder;

public class ProductVersion {
    
    private final Product product;
    private Collection<Request> packs;
    private final Collection<Component> components;
    private final String status;
    private final String name;
    
    private final String executable;
    
    public ProductVersion(Product product, Collection<Request> packs, Collection<Component> components,
            String executable) {
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
    
    public ProductVersion(Product product, String status, String name, URL updateSite) {
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
        String executable = memento.getExecutable();
        return new ProductVersion(product, packs, components, executable);
    }
    
    public ProductVersionMemento toMemento() {
        Builder b = ProductVersionMemento.newBuilder().setProduct(product.toMemento()).setExecutable(
                executable);
        for (Request r : packs)
            b.addPack(r.toMemento());
        for (Component c : components)
            b.addComponent(c.toMemento());
        return b.build();
    }
    
    public String executable() {
        return executable; //> relative
    }
    
    public Collection<ComponentFile> files() {
        Collection<ComponentFile> files = newHashSet();
        for (Component component : components)
            files.addAll(component.files());
        return files;
    }
}
