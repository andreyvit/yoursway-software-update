package com.yoursway.autoupdater.localrepository;

import static com.yoursway.utils.log.LogEntryType.ERROR;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.yoursway.autoupdater.auxiliary.AutoupdaterException;
import com.yoursway.autoupdater.auxiliary.ErrorsListener;
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
import com.yoursway.autoupdater.localrepository.internal.LocalProductVersion;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.LocalProductMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.LocalRepositoryMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.LocalRepositoryMemento.Builder;
import com.yoursway.utils.EventSource;
import com.yoursway.utils.io.InputStreamConsumer;
import com.yoursway.utils.io.OutputStreamConsumer;
import com.yoursway.utils.log.Log;
import com.yoursway.utils.storage.atomic.AtomicFile;

public class LocalRepository {
    
    private final File place;
    private final Map<ProductDefinition, LocalProduct> products = new HashMap<ProductDefinition, LocalProduct>();
    private final Installer installer;
    private final FileLibrary fileLibrary;
    
    private final UpdatableApplicationProductFeaturesProvider featuresProvider;
    
    ErrorsAggregator errors = new ErrorsAggregator();
    
    private boolean initialized = false;
    private final LocalRepositoryChangerCallback lrcc = new LocalRepositoryChangerCallback() {
        public void localRepositoryChanged() {
            try {
                if (initialized)
                    save();
            } catch (IOException e) {
                e.printStackTrace(); //!
            }
        }
    };
    
    private LocalRepository(UpdatableApplicationProductFeaturesProvider featuresProvider,
            Installer installer, File placeDir) throws IOException {
        
        if (featuresProvider == null)
            throw new NullPointerException("featuresProvider is null");
        if (placeDir == null)
            throw new NullPointerException("placeDir is null");
        this.featuresProvider = featuresProvider;
        place = placeDir;
        
        Downloader downloader = new DownloaderImpl();
        fileLibrary = new FileLibraryImpl(downloader, new File(place, "fileLibrary"));
        this.installer = installer;
    }
    
    public EventSource<ErrorsListener> errors() {
        return errors;
    }
    
    public void startUpdating(ProductVersionDefinition version, UpdatingListener listener)
            throws AutoupdaterException {
        ProductDefinition productDefinition = version.product();
        LocalProduct localProduct = products.get(productDefinition);
        if (localProduct == null) {
            localProduct = new LocalProduct(productDefinition, fileLibrary, installer, featuresProvider, lrcc);
            add(localProduct);
        }
        
        try {
            localProduct.startUpdating(version, listener);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AutoupdaterException(e);
        }
    }
    
    private void add(LocalProduct product) {
        product.errors().addListener(errors);
        products.put(product.definition(), product);
        lrcc.localRepositoryChanged();
    }
    
    public void atStartup() throws AutoupdaterException {
        try {
            if (!mementoFile().exists())
                return;
            
            atomicMementoFile().read(new InputStreamConsumer() {
                public void run(InputStream in) throws IOException {
                    LocalRepositoryMemento memento = LocalRepositoryMemento.parseFrom(in);
                    fromMemento(memento);
                }
            });
        } catch (Throwable e) {
            e.printStackTrace(); //!
            
            products.clear();
            Log.write("Cannot load localrepo. New localrepo created.", ERROR);
        } finally {
            initialized = true;
        }
        
        for (LocalProduct product : products.values())
            product.atStartup();
    }
    
    private void save() throws IOException {
        atomicMementoFile().overwrite(new OutputStreamConsumer() {
            public void run(OutputStream out) throws IOException {
                toMemento().writeTo(out);
            }
        });
    }
    
    private AtomicFile atomicMementoFile() {
        return new AtomicFile(mementoFile());
    }
    
    private File mementoFile() {
        return new File(place, "localRepository");
    }
    
    private void fromMemento(LocalRepositoryMemento memento) {
        for (LocalProductMemento m : memento.getProductList()) {
            LocalProduct product = new LocalProduct(m, fileLibrary, installer, featuresProvider, lrcc);
            add(product);
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
            e.printStackTrace(); //!
            throw new LocalRepositoryException(e);
        }
    }
    
    private LocalProductVersion getLocalVersion(ProductVersionDefinition version) {
        LocalProduct product = products.get(version.product());
        if (product == null)
            return null;
        return product.getLocalVersion(version);
    }
    
    public boolean hasLocalVersion(ProductVersionDefinition version) {
        return getLocalVersion(version) != null;
    }
    
    public void addUpdatingListener(ProductVersionDefinition version, UpdatingListener listener) {
        getLocalVersion(version).events().addListener(listener);
    }
    
    public boolean isVersionFailed(ProductVersionDefinition version) {
        return getLocalVersion(version).failed();
    }
    
}
