package com.yoursway.autoupdater.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import com.yoursway.autoupdater.auxiliary.Product;
import com.yoursway.autoupdater.auxiliary.ProductVersion;
import com.yoursway.autoupdater.auxiliary.Suite;
import com.yoursway.autoupdater.localrepository.LocalRepository;
import com.yoursway.utils.log.Log;
import com.yoursway.utils.log.TcpIpLogger;

public class ConsoleDemo {
    
    public static void main(String[] args) {
        Log.setLogger(new TcpIpLogger());
        
        try {
            URL updateSite = new URL(args[0]);
            System.out.println("Update site: " + updateSite);
            
            String suiteName = args[1];
            System.out.println("Suite name: " + suiteName);
            
            Suite suite = new Suite(updateSite, suiteName);
            
            System.out.println();
            System.out.println("Select product:");
            for (Product product : suite.products())
                System.out.println(product.name());
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String productName = reader.readLine();
            System.out.println();
            
            System.out.println("Select version:");
            Iterable<ProductVersion> versions = suite.productVersions(productName);
            for (ProductVersion version : versions)
                System.out.println(version.name() + " - " + version);
            
            String versionName = reader.readLine();
            System.out.println();
            
            ProductVersion version = productVersion(versions, versionName);
            LocalRepository localRepository = new LocalRepository();
            localRepository.startUpdating(version);
            
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    private static ProductVersion productVersion(Iterable<ProductVersion> versions, String versionName) {
        for (ProductVersion version : versions)
            if (version.name().equals(versionName))
                return version;
        return null;
    }
}
