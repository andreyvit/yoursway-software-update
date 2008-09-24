package com.yoursway.autoupdater.core.auxiliary;

import java.util.Collection;

import com.yoursway.autoupdater.core.auxiliary.ComponentDefinition;
import com.yoursway.autoupdater.core.auxiliary.ProductDefinition;
import com.yoursway.autoupdater.core.auxiliary.ProductVersionDefinition;
import com.yoursway.autoupdater.core.filelibrary.Request;

public class AuxiliaryUtils {
    
    public static ProductVersionDefinition createProductVersionDefinition(ProductDefinition product,
            String name, String releaseType, Collection<Request> packs,
            Collection<ComponentDefinition> components, String executable) {
        
        return new ProductVersionDefinition(product, name, releaseType, packs, components, executable);
    }
    
}
