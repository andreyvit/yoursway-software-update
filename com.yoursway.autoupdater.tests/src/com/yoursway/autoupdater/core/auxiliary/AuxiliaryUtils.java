package com.yoursway.autoupdater.core.auxiliary;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import com.yoursway.autoupdater.core.filelibrary.Request;

public class AuxiliaryUtils {
    
    public static ProductVersionDefinition createProductVersionDefinition(ProductDefinition product,
            String name, String releaseType, Collection<Request> packs,
            Collection<ComponentDefinition> components, String executable) {
        
        return new ProductVersionDefinition(product, name, releaseType, packs, components, executable);
    }
    
    public static ProductDefinition fakeProductDefinition() throws MalformedURLException {
        return new ProductDefinition("UNNAMED", new URL("http://localhost/update-site/"));
    }
    
}
