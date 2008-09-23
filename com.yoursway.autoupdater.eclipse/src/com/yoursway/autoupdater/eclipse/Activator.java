package com.yoursway.autoupdater.eclipse;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.yoursway.autoupdater.gui.controller.UpdaterController;

public class Activator extends AbstractUIPlugin {
    
    static UpdaterController controller;
    
    public Activator() {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void start(BundleContext context) throws Exception {
        controller = new UpdaterController(new UpdatableEclipse());
        controller.onStart();
        
        super.start(context);
    }
    
}
