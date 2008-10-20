package com.yoursway.tinyupdater;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
    
    private static Activator instance;
    private BundleContext context;
    
    public void start(BundleContext context) throws Exception {
        this.context = context;
        instance = this;
    }
    
    public static Activator instance() {
        return instance;
    }
    
    public void stop(BundleContext context) throws Exception {
        instance = null;
    }
    
    public Bundle getBundle() {
        return context.getBundle();
    }
    
}
