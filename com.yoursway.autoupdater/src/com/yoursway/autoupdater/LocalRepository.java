package com.yoursway.autoupdater;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.yoursway.autoupdater.auxiliary.Product;
import com.yoursway.autoupdater.auxiliary.ProductVersion;
import com.yoursway.autoupdater.internal.ProductState;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.LocalRepositoryMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductStateMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.LocalRepositoryMemento.Builder;

public class LocalRepository {
    
    private final Map<Product, ProductState> products = new HashMap<Product, ProductState>();
    
    public void startUpdating(ProductVersion version) {
        ProductState product = products.get(version.product());
        product.startUpdating(version);
    }
    
    public void atStartup() {
        InputStream in = null; //> get from storage
        try {
            LocalRepositoryMemento memento = LocalRepositoryMemento.parseFrom(in);
            fromMemento(memento);
        } catch (IOException e) {
            //! crash autoupdater
            e.printStackTrace();
        }
        
        for (ProductState product : products.values())
            product.continueWork();
    }
    
    private void fromMemento(LocalRepositoryMemento memento) {
        for (ProductStateMemento m : memento.getProductList()) {
            ProductState state = ProductState.fromMemento(m);
            products.put(state.product(), state);
        }
    }
    
    private LocalRepositoryMemento toMemento() {
        Builder b = LocalRepositoryMemento.newBuilder();
        for (ProductState product : products.values())
            b.addProduct(product.toMemento());
        return b.build();
    }
}
