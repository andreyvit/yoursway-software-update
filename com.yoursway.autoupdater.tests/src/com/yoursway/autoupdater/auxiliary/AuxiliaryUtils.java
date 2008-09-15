package com.yoursway.autoupdater.auxiliary;

import java.util.Collection;

import com.yoursway.autoupdater.filelibrary.Request;

public class AuxiliaryUtils {
    
    public static ProductVersionDefinition createProductVersionDefinition(ProductDefinition product,
            String name, String releaseType, Collection<Request> packs,
            Collection<ComponentDefinition> components, String executable) {
        
        return new ProductVersionDefinition(product, name, releaseType, packs, components, executable);
    }
    
}
