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
import com.yoursway.utils.annotations.Nullable;

public class ProductVersionDefinition {
    
    private final ProductDefinition product;
    private final String status;
    private final String name;
    
    private final String executable;
    
    private boolean damaged;
    
    @Nullable
    private Collection<Request> packs;
    
    private final Collection<ComponentDefinition> components = newLinkedList();
    private ComponentDefinition installer;
    
    @Deprecated
    public ProductVersionDefinition(ProductDefinition product, @Nullable Collection<Request> packs,
            Collection<ComponentDefinition> components, String executable) {
        if (product == null)
            throw new NullPointerException("product is null");
        if (components == null)
            throw new NullPointerException("components is null");
        
        if (packs != null)
            for (Request packRequest : packs)
                if (!packRequest.url().toString().endsWith(".zip"))
                    throw new IllegalArgumentException("packs: A pack filename must ends with .zip");
        
        this.product = product;
        product.addVersion(this);
        
        for (ComponentDefinition component : components)
            addComponent(component);
        this.packs = packs;
        
        status = "";
        name = "";
        
        this.executable = executable;
    }
    
    public ProductVersionDefinition(ProductDefinition product, String status, String name, URL updateSite) {
        this.product = product;
        product.addVersion(this);
        
        packs = null;
        
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
        
        if (component.isInstaller()) {
            if (installer == null)
                installer = component;
            else
                throw new IllegalArgumentException("The product version have an installer already.");
        }
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
        Collection<Request> packs = null;
        for (RequestMemento m : memento.getPackList()) {
            if (packs == null)
                packs = newLinkedList();
            
            packs.add(Request.fromMemento(m));
        }
        Collection<ComponentDefinition> components = newLinkedList();
        for (ComponentDefinitionMemento m : memento.getComponentList())
            components.add(ComponentDefinition.fromMemento(m));
        String executable = memento.getExecutable();
        return new ProductVersionDefinition(product, packs, components, executable);
    }
    
    public ProductVersionDefinitionMemento toMemento() {
        Builder b = ProductVersionDefinitionMemento.newBuilder().setProduct(product.toMemento())
                .setExecutable(executable);
        if (packs != null)
            for (Request r : packs)
                b.addPack(r.toMemento());
        for (ComponentDefinition c : components)
            b.addComponent(c.toMemento());
        return b.build();
    }
    
    public ComponentFile executable() throws Exception {
        throw new UnsupportedOperationException();
        //throw new Exception("The product version hasn't executable");
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
    
    public ComponentDefinition installer() throws Exception {
        if (installer == null)
            throw new Exception("The product version doesn't have installer."); //?
            
        return installer;
    }
    
}
