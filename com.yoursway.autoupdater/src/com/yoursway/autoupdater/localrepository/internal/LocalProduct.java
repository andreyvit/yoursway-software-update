package com.yoursway.autoupdater.localrepository.internal;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.yoursway.autoupdater.auxiliary.ComponentDefinition;
import com.yoursway.autoupdater.auxiliary.ComponentStopper;
import com.yoursway.autoupdater.auxiliary.ProductDefinition;
import com.yoursway.autoupdater.auxiliary.ProductVersionDefinition;
import com.yoursway.autoupdater.auxiliary.UpdatableApplication;
import com.yoursway.autoupdater.filelibrary.FileLibrary;
import com.yoursway.autoupdater.filelibrary.OrderManager;
import com.yoursway.autoupdater.filelibrary.Request;
import com.yoursway.autoupdater.installer.Installer;
import com.yoursway.autoupdater.localrepository.UpdatingListener;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.LocalProductMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.LocalProductVersionMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.LocalProductMemento.Builder;
import com.yoursway.utils.log.Log;

public class LocalProduct {
    
    private final ProductDefinition definition;
    
    private final Map<ProductVersionDefinition, LocalProductVersion> versions = new HashMap<ProductVersionDefinition, LocalProductVersion>();
    
    private final FileLibrary fileLibrary;
    final OrderManager orderManager;
    final Installer installer;
    
    private final UpdatableApplication app;
    
    public LocalProduct(LocalProductMemento memento, FileLibrary fileLibrary, Installer installer,
            UpdatableApplication app) {
        this.orderManager = fileLibrary.orderManager();
        this.fileLibrary = fileLibrary;
        this.installer = installer;
        
        definition = ProductDefinition.fromMemento(memento.getDefinition());
        for (LocalProductVersionMemento m : memento.getVersionList()) {
            try {
                LocalProductVersion version = LocalProductVersion.fromMemento(m, this);
                versions.put(version.definition, version);
            } catch (MalformedURLException e) {
                e.printStackTrace(); //!
            }
        }
        
        this.app = app;
    }
    
    public LocalProduct(ProductDefinition definition, FileLibrary fileLibrary, Installer installer,
            UpdatableApplication app) {
        
        if (definition == null)
            throw new NullPointerException("definition is null");
        if (fileLibrary == null)
            throw new NullPointerException("fileLibrary is null");
        if (installer == null)
            throw new NullPointerException("installer is null");
        if (app == null)
            throw new NullPointerException("app is null");
        
        this.orderManager = fileLibrary.orderManager();
        this.fileLibrary = fileLibrary;
        this.installer = installer;
        
        this.definition = definition;
        
        this.app = app;
    }
    
    public void startUpdating(ProductVersionDefinition versionDefinition, UpdatingListener listener) {
        if (updating())
            throw new IllegalStateException("Updating of the product has started already.");
        
        Log.write("Starting updating to version " + versionDefinition);
        
        LocalProductVersion localVersion = versions.get(versionDefinition);
        if (localVersion != null) {
            localVersion.setListener(listener);
            localVersion.startUpdating();
        } else {
            localVersion = new LocalProductVersion(this, versionDefinition, listener);
            versions.put(versionDefinition, localVersion);
            fileLibrary.events().addListener(localVersion);
            orderManager.register(localVersion);
            localVersion.continueWork();
        }
    }
    
    private boolean updating() {
        for (LocalProductVersion version : versions.values())
            if (version.updating())
                return true;
        return false;
    }
    
    public void continueWork() {
        for (LocalProductVersion version : versions.values())
            version.continueWork();
    }
    
    public ProductDefinition definition() {
        return definition;
    }
    
    public LocalProductMemento toMemento() {
        Builder b = LocalProductMemento.newBuilder().setDefinition(definition.toMemento());
        for (LocalProductVersion version : versions.values())
            b.addVersion(version.toMemento());
        return b.build();
    }
    
    public ProductVersionDefinition currentVersion() {
        //>
        
        Collection<Request> requests = Collections.emptyList();
        Collection<ComponentDefinition> components = Collections.emptyList();
        return new ProductVersionDefinition(definition, requests, components, ""); //! executable
    }
    
    public ComponentStopper componentStopper() {
        return app.componentStopper(definition.name());
    }
    
    public File rootFolder() throws IOException {
        return app.rootFolder(definition.name());
        // return YsFileUtils.createTempFolder("autoupdater.appRootFolder", null);
    }
    
}
