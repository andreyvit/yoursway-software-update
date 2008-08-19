package com.yoursway.autoupdater.localrepository.internal;

import java.util.HashMap;
import java.util.Map;

import com.yoursway.autoupdater.auxiliary.Product;
import com.yoursway.autoupdater.auxiliary.ProductVersion;
import com.yoursway.autoupdater.filelibrary.FileLibrary;
import com.yoursway.autoupdater.filelibrary.OrderManager;
import com.yoursway.autoupdater.internal.installer.Installer;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductStateMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductStateMemento.Builder;

public class ProductState {
    
    private final Product product;
    
    private final Map<ProductVersion, ProductVersionState> versions = new HashMap<ProductVersion, ProductVersionState>();
    
    private final FileLibrary fileLibrary;
    final OrderManager orderManager;
    final Installer installer;
    
    public ProductState(ProductStateMemento memento, FileLibrary fileLibrary, Installer installer) {
        this.orderManager = fileLibrary.orderManager();
        this.fileLibrary = fileLibrary;
        this.installer = installer;
        
        product = Product.fromMemento(memento.getProduct());
        for (ProductVersionStateMemento m : memento.getVersionList()) {
            ProductVersionStateWrap state = ProductVersionStateWrap.fromMemento(m, this);
            versions.put(state.version, state);
        }
    }
    
    public ProductState(Product product, FileLibrary fileLibrary, Installer installer) {
        this.orderManager = fileLibrary.orderManager();
        this.fileLibrary = fileLibrary;
        this.installer = installer;
        
        this.product = product;
    }
    
    public void startUpdating(ProductVersion version) {
        if (updating())
            throw new IllegalStateException("Updating of the product has started already.");
        
        ProductVersionState state = versions.get(version);
        if (state != null)
            state.startUpdating();
        else {
            state = new ProductVersionStateWrap(version, this);
            versions.put(version, state);
            fileLibrary.events().addListener(state);
            orderManager.register(state);
            state.continueWork();
        }
    }
    
    private boolean updating() {
        for (ProductVersionState version : versions.values())
            if (version.updating())
                return true;
        return false;
    }
    
    public void continueWork() {
        for (ProductVersionState version : versions.values())
            version.continueWork();
    }
    
    public Product product() {
        return product;
    }
    
    public ProductStateMemento toMemento() {
        Builder b = ProductStateMemento.newBuilder().setProduct(product.toMemento());
        for (ProductVersionState version : versions.values())
            b.addVersion(version.toMemento());
        return b.build();
    }
    
}
