package com.yoursway.autoupdater.internal;

import java.util.HashMap;
import java.util.Map;

import com.yoursway.autoupdater.auxiliary.ProductVersion;

public class ProductState {
    
    private final Map<ProductVersion, ProductVersionState> versions = new HashMap<ProductVersion, ProductVersionState>();
    
    public void startUpdating(ProductVersion version) {
        if (updating())
            throw new IllegalStateException("Updating of the product has started already.");
        
        ProductVersionState state = versions.get(version);
        if (state == null) {
            state = new ProductVersionState(version);
            versions.put(version, state);
        }
        
        state.startUpdating();
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
    
}
