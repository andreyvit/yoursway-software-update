package com.yoursway.autoupdater.core.auxiliary;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static com.yoursway.utils.log.LogEntryType.ERROR;
import static com.yoursway.utils.os.YsOSUtils.isMacOSX;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import com.yoursway.utils.log.Log;

public class SuiteDefinition {
    
    private static final String SUITES_PATH = "suites/";
    
    private final URL updateSite;
    private final String name;
    private final Map<String, ProductDefinition> products = newHashMap();
    
    SuiteDefinition(URL updateSite, String name) throws InvalidFileFormatException, IOException {
        this.updateSite = updateSite;
        this.name = name;
        
        InputStream stream = null;
        try {
            URL versions = new URL(updateSite + SUITES_PATH + name + "/" + versionsFilename());
            stream = versions.openStream();
            DefinitionReader reader = new DefinitionReader(stream);
            
            ProductVersionDefinition productVersion = null;
            while (true) {
                String[] fields = reader.readLine();
                if (fields == null)
                    break;
                
                String type = fields[0];
                if (type.equals("PV")) {
                    productVersion = null;
                    try {
                        productVersion = addProductVersion(fields[1], fields[2], fields[3]);
                    } catch (Throwable e) {
                        Log.write("Cannot add product version to suite definition: "
                                + e.getClass().getSimpleName(), ERROR);
                    }
                } else {
                    if (productVersion != null) {
                        Log.write("Cannot understand line: " + fields);
                        productVersion.damage();
                        productVersion = null;
                    }
                }
            }
            
        } finally {
            if (stream != null)
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace(); //!
                }
        }
        
    }
    
    public String getName() {
		return name;
	}

	private static String versionsFilename() {
        String platform = isMacOSX() ? "mac" : "win";
        return "versions_" + platform + ".txt";
    }
    
    public static SuiteDefinition load(String updateSite, String name) throws SuiteDefinitionLoadingException {
        try {
            URL url = new URL(updateSite);
            return new SuiteDefinition(url, name);
        } catch (Throwable e) {
            throw new SuiteDefinitionLoadingException(updateSite, name, e);
        }
    }
    
    private ProductVersionDefinition addProductVersion(String productName, String releaseType,
            String versionName) {
        ProductDefinition product = products.get(productName);
        if (product == null) {
            product = new ProductDefinition(productName, updateSite);
            products.put(productName, product);
        }
        return new ProductVersionDefinition(product, releaseType, versionName);
    }
    
    public Iterable<ProductDefinition> products() {
        return products.values();
    }
    
    public Iterable<ProductVersionDefinition> versions(String productName) {
        ProductDefinition product = products.get(productName);
        if (product == null)
            throw new IllegalArgumentException("productName");
        return product.versions();
    }
    
    public Collection<ProductVersionDefinition> versions() {
        Collection<ProductVersionDefinition> versions = newLinkedList();
        for (ProductDefinition product : products.values())
            versions.addAll(product.versions());
        return versions;
    }
    
}
