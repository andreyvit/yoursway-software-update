package com.yoursway.autoupdater.auxiliary;

import static com.google.common.collect.Maps.newHashMap;
import static com.yoursway.utils.log.LogEntryType.ERROR;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import com.yoursway.utils.log.Log;

public class Suite {
    
    private static final String SUITES_PATH = "suites/";
    private static final String VERSIONS_FILENAME = "versions_mac.txt";
    
    private final URL updateSite;
    private final String name;
    private final Map<String, Product> products = newHashMap();
    
    public Suite(URL updateSite, String name) throws InvalidFileFormatException, MalformedURLException {
        this.updateSite = updateSite;
        this.name = name;
        
        InputStream stream = null;
        try {
            URL versions = new URL(updateSite + SUITES_PATH + name + "/" + VERSIONS_FILENAME);
            stream = versions.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            
            ProductVersion productVersion = null;
            while (true) {
                String line = reader.readLine();
                if (line == null)
                    break;
                
                if (line.length() == 0)
                    continue;
                
                String[] fields = line.split("\t");
                
                String type = fields[0];
                if (type.equals("PV")) {
                    productVersion = null;
                    try {
                        productVersion = addProductVersion(fields[1], fields[2], fields[3]);
                    } catch (Throwable e) {
                        Log.write("Cannot add product version to suite: " + e.getClass().getSimpleName(),
                                ERROR);
                    }
                } else if (type.equals("CVB")) {
                    String componentName = fields[1];
                    try {
                        addComponentVersion(productVersion, componentName);
                    } catch (Exception e) {
                        Log.write("Cannot add component " + componentName + " to product version "
                                + productVersion + ": " + e.getClass().getSimpleName(), ERROR);
                    }
                } else
                    throw new InvalidFileFormatException(versions); //! ignore line?
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace(); //!
        } catch (IOException e) {
            e.printStackTrace(); //!
        } finally {
            if (stream != null)
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace(); //!
                }
        }
        
    }
    
    private void addComponentVersion(ProductVersion productVersion, String name) throws IOException,
            InvalidFileFormatException {
        productVersion.addComponentVersion(new Component(updateSite, name));
        
    }
    
    private ProductVersion addProductVersion(String productName, String status, String versionName) {
        Product product = products.get(productName);
        if (product == null) {
            product = new Product(productName);
            products.put(productName, product);
        }
        return new ProductVersion(product, status, versionName, updateSite);
    }
    
    public Iterable<Product> products() {
        return products.values();
    }
    
    public Iterable<ProductVersion> productVersions(String productName) {
        Product product = products.get(productName);
        if (product == null)
            throw new IllegalArgumentException("productName");
        return product.versions();
    }
    
}
