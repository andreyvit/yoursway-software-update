package com.yoursway.autoupdater.eclipse;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.yoursway.autoupdater.gui.controller.UpdaterController;
import com.yoursway.utils.log.FileLogger;
import com.yoursway.utils.log.Log;

public class Activator extends AbstractUIPlugin {
    
    static UpdaterController controller;
    
    public Activator() {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void start(BundleContext context) throws Exception {
        Log.setLogger(new FileLogger("updatable-eclipse"));
        
        controller = new UpdaterController(new UpdatableEclipse());
        controller.atStartup();
        
        super.start(context);
    }
    
}
