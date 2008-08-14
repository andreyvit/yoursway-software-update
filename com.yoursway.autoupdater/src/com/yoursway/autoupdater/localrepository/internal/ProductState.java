package com.yoursway.autoupdater.localrepository.internal;

import java.util.HashMap;
import java.util.Map;

import com.yoursway.autoupdater.auxiliary.Product;
import com.yoursway.autoupdater.auxiliary.ProductVersion;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductStateMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductStateMemento.Builder;

public class ProductState {
    
    private final Product product;
    
    private final Map<ProductVersion, ProductVersionState> versions = new HashMap<ProductVersion, ProductVersionState>();
    
    private ProductState(ProductStateMemento memento) {
        product = Product.fromMemento(memento.getProduct());
        for (ProductVersionStateMemento m : memento.getVersionList()) {
            ProductVersionStateWrap state = ProductVersionStateWrap.fromMemento(m, this);
            versions.put(state.version(), state);
        }
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
    
    public static ProductState fromMemento(ProductStateMemento memento) {
        return new ProductState(memento);
    }
    
    public ProductStateMemento toMemento() {
        Builder b = ProductStateMemento.newBuilder().setProduct(product.toMemento());
        for (ProductVersionState version : versions.values())
            b.addVersion(version.toMemento());
        return b.build();
    }
    
    ProductVersionStateWrap currentVersionStateOrNull() {
        for (ProductVersionState version : versions.values())
            if (version.isCurrent())
                return (ProductVersionStateWrap) version;
        return null;
    }
}
