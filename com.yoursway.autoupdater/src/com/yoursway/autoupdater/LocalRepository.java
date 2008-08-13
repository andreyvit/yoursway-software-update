package com.yoursway.autoupdater;

import java.util.HashMap;
import java.util.Map;

import com.yoursway.autoupdater.auxiliary.Product;
import com.yoursway.autoupdater.auxiliary.ProductVersion;
import com.yoursway.autoupdater.internal.ProductState;

public class LocalRepository {
    
    private final Map<Product, ProductState> products = new HashMap<Product, ProductState>();
    
    public void startUpdating(ProductVersion version) {
        ProductState product = products.get(version.product());
        product.startUpdating(version);
    }
    
    public void atStartup() {
        //> get product states
        
        for (ProductState product : products.values())
            product.continueWork();
    }
    
}
