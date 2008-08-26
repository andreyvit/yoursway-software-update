package com.yoursway.autoupdater.localrepository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.yoursway.autoupdater.auxiliary.Product;
import com.yoursway.autoupdater.auxiliary.ProductVersion;
import com.yoursway.autoupdater.filelibrary.FileLibrary;
import com.yoursway.autoupdater.filelibrary.FileLibraryImpl;
import com.yoursway.autoupdater.filelibrary.downloader.Downloader;
import com.yoursway.autoupdater.filelibrary.downloader.DownloaderImpl;
import com.yoursway.autoupdater.installer.Installer;
import com.yoursway.autoupdater.installer.InstallerImpl;
import com.yoursway.autoupdater.localrepository.internal.ProductState;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.LocalRepositoryMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductStateMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.LocalRepositoryMemento.Builder;
import com.yoursway.utils.YsFileUtils;

public class LocalRepository {
    
    private final Map<Product, ProductState> products = new HashMap<Product, ProductState>();
    private final Installer installer;
    private final FileLibrary fileLibrary;
    
    public LocalRepository() throws IOException {
        Downloader downloader = new DownloaderImpl();
        File place = YsFileUtils.createTempFolder("localrepository.filelibrary.place", null);
        place.mkdir();
        fileLibrary = new FileLibraryImpl(downloader, place);
        installer = new InstallerImpl();
    }
    
    public LocalRepository(Installer installer) throws IOException {
        Downloader downloader = new DownloaderImpl();
        File place = YsFileUtils.createTempFolder("localrepository.filelibrary.place", null);
        place.mkdir();
        fileLibrary = new FileLibraryImpl(downloader, place);
        this.installer = installer;
    }
    
    public void startUpdating(ProductVersion version) {
        Product product = version.product();
        ProductState productState = products.get(product);
        if (productState == null) {
            productState = new ProductState(product, fileLibrary, installer);
            products.put(product, productState);
        }
        productState.startUpdating(version);
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
        
        for (ProductState product : products.values())
            product.continueWork();
    }
    
    private void fromMemento(LocalRepositoryMemento memento) {
        for (ProductStateMemento m : memento.getProductList()) {
            ProductState state = new ProductState(m, fileLibrary, installer);
            products.put(state.product(), state);
        }
    }
    
    private LocalRepositoryMemento toMemento() {
        Builder b = LocalRepositoryMemento.newBuilder();
        for (ProductState product : products.values())
            b.addProduct(product.toMemento());
        return b.build();
    }
}
