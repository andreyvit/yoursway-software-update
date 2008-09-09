package com.yoursway.autoupdater.auxiliary;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static com.yoursway.utils.log.LogEntryType.ERROR;
import static com.yoursway.utils.os.YsOSUtils.isMacOSX;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            
            ProductVersionDefinition productVersion = null;
            while (true) {
                String line = reader.readLine();
                if (line == null)
                    break;
                if (line.length() == 0)
                    continue;
                
                String[] fields = line.split("\t");
                if (fields.length == 0)
                    continue;
                
                String type = fields[0];
                if (type.equals("PV")) {
                    productVersion = null;
                    try {
                        productVersion = addProductVersion(fields[1], fields[2], fields[3]);
                    } catch (Throwable e) {
                        Log.write("Cannot add product version to suite definition: "
                                + e.getClass().getSimpleName(), ERROR);
                    }
                } else if (type.equals("CVB")) {
                    String componentName = null;
                    try {
                        componentName = fields[1];
                        addComponent(productVersion, componentName);
                    } catch (Throwable e) {
                        if (productVersion != null)
                            productVersion.damage();
                        
                        String component = componentName != null ? " " + componentName : "";
                        String version = productVersion != null ? " " + productVersion : "";
                        Log.write("Cannot add component" + component + " to product version" + version
                                + " definition: " + e.getClass().getSimpleName(), ERROR);
                    }
                } else {
                    if (productVersion != null) {
                        Log.write("Cannot understand line: " + line);
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
    
    private void addComponent(ProductVersionDefinition productVersion, String name) throws IOException,
            InvalidFileFormatException {
        
        ComponentDefinition component = new ComponentDefinition(updateSite, name);
        productVersion.addComponent(component);
    }
    
    private ProductVersionDefinition addProductVersion(String productName, String releaseType,
            String versionName) {
        ProductDefinition product = products.get(productName);
        if (product == null) {
            product = new ProductDefinition(productName);
            products.put(productName, product);
        }
        return new ProductVersionDefinition(product, releaseType, versionName, updateSite);
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
