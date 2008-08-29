package com.yoursway.autoupdater.localrepository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.yoursway.autoupdater.auxiliary.ProductDefinition;
import com.yoursway.autoupdater.auxiliary.ProductVersionDefinition;
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
    
    public LocalRepository() throws IOException {
        Downloader downloader = new DownloaderImpl();
        File place = YsFileUtils.createTempFolder("localrepository.filelibrary.place", null);
        place.mkdir();
        fileLibrary = new FileLibraryImpl(downloader, place);
        installer = new ExternalInstaller();
    }
    
    public LocalRepository(Installer installer) throws IOException {
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
            localProduct = new LocalProduct(productDefinition, fileLibrary, installer);
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
            LocalProduct product = new LocalProduct(m, fileLibrary, installer);
            products.put(product.definition(), product);
        }
    }
    
    private LocalRepositoryMemento toMemento() {
        Builder b = LocalRepositoryMemento.newBuilder();
        for (LocalProduct product : products.values())
            b.addProduct(product.toMemento());
        return b.build();
    }
    
    public static LocalRepository createForGUI() throws LocalRepositoryException {
        try {
            ExternalInstaller installer = new ExternalInstaller(true);
            return new LocalRepository(installer);
        } catch (Throwable e) {
            throw new LocalRepositoryException(e);
        }
    }
}
