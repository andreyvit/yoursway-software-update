package com.yoursway.autoupdater.auxiliary;

import static com.google.common.collect.Lists.newLinkedList;
import static com.yoursway.autoupdater.auxiliary.ComponentDefinition.PACKS_PATH;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import com.yoursway.autoupdater.filelibrary.Request;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ComponentDefinitionMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionDefinitionMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.RequestMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionDefinitionMemento.Builder;
import com.yoursway.utils.annotations.Nullable;

public class ProductVersionDefinition {
    
    private final ProductDefinition product;
    private final String name;
    private final String releaseType;
    
    private final String executable;
    
    private boolean damaged;
    
    @Nullable
    private static Collection<Request> packs;
    
    private final Collection<ComponentDefinition> components = newLinkedList();
    private ComponentDefinition installer;
    
    ProductVersionDefinition(ProductDefinition product, String name, String releaseType,
            @Nullable Collection<Request> packs, Collection<ComponentDefinition> components, String executable) {
        if (product == null)
            throw new NullPointerException("product is null");
        if (components == null)
            throw new NullPointerException("components is null");
        
        if (packs != null)
            for (Request packRequest : packs)
                if (!packRequest.url().toString().endsWith(".zip"))
                    throw new IllegalArgumentException("packs: A pack filename must ends with .zip");
        
        for (ComponentDefinition component : components)
            addComponent(component);
        this.packs = packs;
        
        this.releaseType = releaseType;
        this.name = name;
        
        this.executable = executable;
        
        this.product = product;
        product.addVersion(this);
    }
    
    public ProductVersionDefinition(ProductDefinition product, String releaseType, String name) {
        packs = null;
        
        this.releaseType = releaseType;
        this.name = name;
        
        executable = System.getProperty("user.dir") + "/afterupdate.jar";
        
        this.product = product;
        product.addVersion(this);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProductVersionDefinition other = (ProductVersionDefinition) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (product == null) {
            if (other.product != null)
                return false;
        } else if (!product.equals(other.product))
            return false;
        if (releaseType == null) {
            if (other.releaseType != null)
                return false;
        } else if (!releaseType.equals(other.releaseType))
            return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((product == null) ? 0 : product.hashCode());
        result = prime * result + ((releaseType == null) ? 0 : releaseType.hashCode());
        return result;
    }
    
    @Override
    public String toString() {
        return product + "-" + name + "-" + releaseType;
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
        return new ProductVersionDefinition(product, memento.getName(), memento.getType(), packs, components,
                executable);
    }
    
    public ProductVersionDefinitionMemento toMemento() {
        Builder b = ProductVersionDefinitionMemento.newBuilder().setProduct(product.toMemento())
                .setExecutable(executable).setName(name).setType(releaseType);
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
    
    public static ProductVersionDefinition loadFrom(URL url, ProductDefinition product) throws IOException,
            InvalidFileFormatException {
        InputStream stream = url.openStream();
        DefinitionReader reader = new DefinitionReader(stream);
        
        String[] firstLine = reader.readLine();
        if (firstLine.length != 4 || !firstLine[0].equals("PV"))
            throw new InvalidFileFormatException(url);
        if (!firstLine[1].equals(product.name()))
            throw new IllegalArgumentException("This version is one of another product");
        
        ProductVersionDefinition definition = new ProductVersionDefinition(product, firstLine[2],
                firstLine[3]);
        
        boolean dontReadNext = false;
        String[] fields = null;
        while (true) {
            if (!dontReadNext)
                fields = reader.readLine();
            else
                dontReadNext = false;
            
            if (fields == null)
                break;
            
            if (fields.length != 2 || !fields[0].equals("CVB"))
                throw new InvalidFileFormatException(url);
            
            String componentName = fields[1];
            List<Request> packs = newLinkedList();
            List<ComponentFile> files = newLinkedList();
            
            while (true) {
                fields = reader.readLine();
                if (fields == null)
                    break;
                
                String type = fields[0];
                if (type.equals("P") || type.equals("F")) {
                    String hash = fields[1];
                    long size = Long.parseLong(fields[2]);
                    if (type.equals("P")) {
                        URL packUrl = new URL(product.updateSite + PACKS_PATH + hash + ".zip");
                        Request request = new Request(packUrl, size, hash);
                        packs.add(request);
                    } else {
                        long modified = Long.parseLong(fields[3]);
                        ComponentFile file = new ComponentFile(hash, size, modified, fields[4], fields[5]);
                        files.add(file);
                    }
                } else {
                    dontReadNext = true;
                    break;
                }
                
            }
            
            ComponentDefinition component = new ComponentDefinition(componentName, packs, files);
            definition.addComponent(component);
        }
        
        return definition;
    }
}
