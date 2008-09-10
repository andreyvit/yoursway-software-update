package com.yoursway.autoupdater.localrepository.internal;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import com.yoursway.autoupdater.auxiliary.ComponentStopper;
import com.yoursway.autoupdater.auxiliary.ProductDefinition;
import com.yoursway.autoupdater.auxiliary.ProductVersionDefinition;
import com.yoursway.autoupdater.auxiliary.UpdatableApplicationProductFeatures;
import com.yoursway.autoupdater.auxiliary.UpdatableApplicationProductFeaturesProvider;
import com.yoursway.autoupdater.filelibrary.FileLibrary;
import com.yoursway.autoupdater.filelibrary.OrderManager;
import com.yoursway.autoupdater.installer.Installer;
import com.yoursway.autoupdater.localrepository.LocalRepositoryChangerCallback;
import com.yoursway.autoupdater.localrepository.UpdatingListener;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.LocalProductMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.LocalProductVersionMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.LocalProductMemento.Builder;
import com.yoursway.utils.annotations.Nullable;
import com.yoursway.utils.log.Log;

public class LocalProduct {
    
    private final ProductDefinition definition;
    
    private final Map<ProductVersionDefinition, LocalProductVersion> versions = new HashMap<ProductVersionDefinition, LocalProductVersion>();
    
    private final FileLibrary fileLibrary;
    final OrderManager orderManager;
    final Installer installer;
    private final LocalRepositoryChangerCallback lrcc;
    
    private final UpdatableApplicationProductFeatures features;
    
    public LocalProduct(LocalProductMemento memento, FileLibrary fileLibrary, Installer installer,
            UpdatableApplicationProductFeaturesProvider featuresProvider, LocalRepositoryChangerCallback lrcc) {
        this.orderManager = fileLibrary.orderManager();
        this.fileLibrary = fileLibrary;
        this.installer = installer;
        this.lrcc = lrcc;
        
        definition = ProductDefinition.fromMemento(memento.getDefinition());
        for (LocalProductVersionMemento m : memento.getVersionList()) {
            try {
                LocalProductVersion version = LocalProductVersion.fromMemento(m, this, lrcc);
                versions.put(version.definition, version);
                registerVersionEvents(version);
            } catch (MalformedURLException e) {
                e.printStackTrace(); //!
            }
        }
        
        features = featuresProvider.getFeatures(definition.name());
    }
    
    public LocalProduct(ProductDefinition definition, FileLibrary fileLibrary, Installer installer,
            UpdatableApplicationProductFeaturesProvider featuresProvider, LocalRepositoryChangerCallback lrcc) {
        
        if (definition == null)
            throw new NullPointerException("definition is null");
        if (fileLibrary == null)
            throw new NullPointerException("fileLibrary is null");
        if (installer == null)
            throw new NullPointerException("installer is null");
        if (lrcc == null)
            throw new NullPointerException("lrcc is null");
        
        this.orderManager = fileLibrary.orderManager();
        this.fileLibrary = fileLibrary;
        this.installer = installer;
        this.lrcc = lrcc;
        
        this.definition = definition;
        
        features = featuresProvider.getFeatures(definition.name());
    }
    
    public void startUpdating(ProductVersionDefinition versionDefinition, @Nullable UpdatingListener listener) {
        if (updating())
            throw new IllegalStateException("Updating of the product has started already.");
        
        Log.write("Starting updating to version " + versionDefinition);
        
        LocalProductVersion localVersion = versions.get(versionDefinition);
        if (localVersion != null) {
            if (listener != null)
                localVersion.events().addListener(listener);
            localVersion.startUpdating();
        } else {
            localVersion = new LocalProductVersion(this, versionDefinition, lrcc);
            if (listener != null)
                localVersion.events().addListener(listener);
            versions.put(versionDefinition, localVersion);
            lrcc.localRepositoryChanged();
            registerVersionEvents(localVersion);
            localVersion.continueWork();
        }
    }
    
    private void registerVersionEvents(LocalProductVersion version) {
        fileLibrary.events().addListener(version);
        orderManager.register(version);
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
    
    public ProductVersionDefinition currentVersion() throws DefinitionException {
        try {
            String vdPath = features.currentVersionDefinitionPath();
            File vdFile = new File(rootFolder(), vdPath);
            return ProductVersionDefinition.loadFrom(vdFile.toURL(), definition);
        } catch (Throwable e) {
            throw new DefinitionException("Cannot load the current version definition", e);
        }
    }
    
    public ComponentStopper componentStopper() {
        return features.componentStopper();
    }
    
    public File rootFolder() throws IOException {
        return features.rootFolder();
    }
    
    public String executablePath() {
        return features.executablePath();
    }
    
    public LocalProductVersion getLocalVersion(ProductVersionDefinition version) {
        return versions.get(version);
    }
    
}
