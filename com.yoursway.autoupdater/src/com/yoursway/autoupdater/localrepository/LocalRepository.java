package com.yoursway.autoupdater.localrepository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.yoursway.autoupdater.auxiliary.ComponentStopper;
import com.yoursway.autoupdater.auxiliary.ProductDefinition;
import com.yoursway.autoupdater.auxiliary.ProductVersionDefinition;
import com.yoursway.autoupdater.auxiliary.SuiteDefinition;
import com.yoursway.autoupdater.auxiliary.UpdatableApplication;
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
    
    private final Map<ProductDefinition, LocalProduct> products = new HashMap<ProductDefinition, LocalProduct>();
    private final Installer installer;
    private final FileLibrary fileLibrary;
    private final UpdatableApplication app;
    
    public LocalRepository() throws IOException {
        this(new ExternalInstaller());
    }
    
    public LocalRepository(UpdatableApplication app, Installer installer) throws IOException {
        this.app = app;
        
        Downloader downloader = new DownloaderImpl();
        File place = YsFileUtils.createTempFolder("localrepository.filelibrary.place", null);
        place.mkdir();
        fileLibrary = new FileLibraryImpl(downloader, place);
        this.installer = installer;
    }
    
    public LocalRepository(Installer installer) throws IOException {
        app = new UpdatableApplicationWithLocalRepository();
        
        Downloader downloader = new DownloaderImpl();
        File place = YsFileUtils.createTempFolder("localrepository.filelibrary.place", null);
        place.mkdir();
        fileLibrary = new FileLibraryImpl(downloader, place);
        this.installer = installer;
    }
    
    public void startUpdating(ProductVersionDefinition version, UpdatingListener listener) {
        ProductDefinition productDefinition = version.product();
        LocalProduct localProduct = products.get(productDefinition);
        if (localProduct == null) {
            localProduct = new LocalProduct(productDefinition, fileLibrary, installer, app);
            products.put(productDefinition, localProduct);
        }
        localProduct.startUpdating(version, listener);
    }
    
    public void atStartup() {
        
        InputStream in = null; //> get from storage
        try {
            LocalRepositoryMemento memento = LocalRepositoryMemento.parseFrom(in);
            fromMemento(memento);
        } catch (IOException e) {
            //! crash autoupdater
            e.printStackTrace();
        }
        
        for (LocalProduct product : products.values())
            product.continueWork();
    }
    
    private void fromMemento(LocalRepositoryMemento memento) {
        for (LocalProductMemento m : memento.getProductList()) {
            LocalProduct product = new LocalProduct(m, fileLibrary, installer, app);
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
            return new LocalRepository(app, installer);
        } catch (Throwable e) {
            throw new LocalRepositoryException(e);
        }
    }
    
    private final class UpdatableApplicationWithLocalRepository implements UpdatableApplication {
        public LocalRepository localRepository() {
            return LocalRepository.this;
        }
        
        public File rootFolder(String productName) {
            throw new UnsupportedOperationException();
        }
        
        public SuiteDefinition suite() {
            throw new UnsupportedOperationException();
        }
        
        public ComponentStopper componentStopper(String productName) {
            return new ComponentStopper() {
                public boolean stop() {
                    System.exit(0);
                    return true;
                }
            };
        }
    }
}
