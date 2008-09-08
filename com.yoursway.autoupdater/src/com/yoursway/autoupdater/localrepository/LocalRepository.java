package com.yoursway.autoupdater.localrepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.yoursway.autoupdater.auxiliary.AutoupdaterException;
import com.yoursway.autoupdater.auxiliary.ProductDefinition;
import com.yoursway.autoupdater.auxiliary.ProductVersionDefinition;
import com.yoursway.autoupdater.auxiliary.UpdatableApplication;
import com.yoursway.autoupdater.auxiliary.UpdatableApplicationProductFeaturesProvider;
import com.yoursway.autoupdater.filelibrary.FileLibrary;
import com.yoursway.autoupdater.filelibrary.FileLibraryImpl;
import com.yoursway.autoupdater.filelibrary.downloader.Downloader;
import com.yoursway.autoupdater.filelibrary.downloader.DownloaderImpl;
import com.yoursway.autoupdater.installer.Installer;
import com.yoursway.autoupdater.installer.external.ExternalInstaller;
import com.yoursway.autoupdater.localrepository.internal.LocalProduct;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.LocalProductMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.LocalRepositoryMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.LocalRepositoryMemento.Builder;
import com.yoursway.utils.YsFileUtils;

public class LocalRepository {
    
    private final File place;
    private final Map<ProductDefinition, LocalProduct> products = new HashMap<ProductDefinition, LocalProduct>();
    private final Installer installer;
    private final FileLibrary fileLibrary;
    
    private final UpdatableApplicationProductFeaturesProvider featuresProvider;
    
    @Deprecated
    public LocalRepository() throws IOException {
        this(new ExternalInstaller());
    }
    
    @Deprecated
    public LocalRepository(Installer installer) throws IOException {
        featuresProvider = UpdatableApplicationProductFeaturesProvider.MOCK;
        
        Downloader downloader = new DownloaderImpl();
        place = YsFileUtils.createTempFolder("localrepository.filelibrary.place", null);
        fileLibrary = new FileLibraryImpl(downloader, new File(place, "fileLibrary"));
        this.installer = installer;
    }
    
    public LocalRepository(UpdatableApplicationProductFeaturesProvider featuresProvider, Installer installer,
            File placeDir) throws IOException {
        
        if (featuresProvider == null)
            throw new NullPointerException("featuresProvider is null");
        if (placeDir == null)
            throw new NullPointerException("placeDir is null");
        this.featuresProvider = featuresProvider;
        place = placeDir;
        
        Downloader downloader = new DownloaderImpl();
        fileLibrary = new FileLibraryImpl(downloader, new File(place, "fileLibrary"));
        this.installer = installer;
        
        atStartup();
    }
    
    public void startUpdating(ProductVersionDefinition version, UpdatingListener listener)
            throws AutoupdaterException {
        ProductDefinition productDefinition = version.product();
        LocalProduct localProduct = products.get(productDefinition);
        if (localProduct == null) {
            localProduct = new LocalProduct(productDefinition, fileLibrary, installer, featuresProvider);
            products.put(productDefinition, localProduct);
        }
        
        try {
            localProduct.startUpdating(version, listener);
        } catch (Exception e) {
            throw new AutoupdaterException(e);
        }
    }
    
    public void atStartup() {
        try {
            InputStream in = new FileInputStream(mementoFile());
            LocalRepositoryMemento memento = LocalRepositoryMemento.parseFrom(in);
            fromMemento(memento);
        } catch (IOException e) {
            //! crash autoupdater
            e.printStackTrace();
        }
        
        for (LocalProduct product : products.values())
            product.continueWork();
    }
    
    private File mementoFile() {
        return new File(place, "localRepository");
    }
    
    private void fromMemento(LocalRepositoryMemento memento) {
        for (LocalProductMemento m : memento.getProductList()) {
            LocalProduct product = new LocalProduct(m, fileLibrary, installer, featuresProvider);
            products.put(product.definition(), product);
        }
    }
    
    private LocalRepositoryMemento toMemento() {
        Builder b = LocalRepositoryMemento.newBuilder();
        for (LocalProduct product : products.values())
            b.addProduct(product.toMemento());
        return b.build();
    }
    
    public static LocalRepository createForGUI(UpdatableApplication app) throws LocalRepositoryException {
        try {
            ExternalInstaller installer = new ExternalInstaller(true);
            return new LocalRepository(app, installer, app.localRepositoryPlace());
        } catch (Throwable e) {
            throw new LocalRepositoryException(e);
        }
    }
    
}
