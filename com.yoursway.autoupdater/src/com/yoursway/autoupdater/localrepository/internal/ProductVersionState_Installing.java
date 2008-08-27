package com.yoursway.autoupdater.localrepository.internal;

import static com.google.common.collect.Maps.newHashMap;
import static com.yoursway.utils.YsFileUtils.createTempFolder;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.yoursway.autoupdater.filelibrary.FileLibraryListener;
import com.yoursway.autoupdater.filelibrary.LibraryState;
import com.yoursway.autoupdater.filelibrary.Request;
import com.yoursway.autoupdater.installer.InstallerException;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento.State;
import com.yoursway.utils.log.Log;

public class ProductVersionState_Installing extends AbstractProductVersionState implements
        FileLibraryListener {
    
    public ProductVersionState_Installing(ProductVersionStateWrap wrap) {
        super(wrap);
    }
    
    @Override
    public void continueWork() {
        Log.write("Ordering files.");
        listener().downloadingStarted();
        orderManager().orderChanged();
    }
    
    @Override
    public Collection<Request> requiredFiles() {
        return version().packs();
    }
    
    @Override
    public void libraryChanged(LibraryState state) {
        Collection<Request> packs = version().packs();
        if (state.filesReady(packs)) {
            Log.write("Files ready.");
            listener().downloadingCompleted();
            
            Collection<File> localPacks = state.getLocalFiles(packs);
            Map<String, File> packsMap = newHashMap();
            for (File file : localPacks) {
                String name = file.getName();
                if (!name.endsWith(".zip"))
                    throw new AssertionError("A pack file name must ends with .zip"); //!
                String hash = name.substring(0, name.length() - 4);
                packsMap.put(hash, file);
            }
            try {
                File extInstallerFolder = createTempFolder("com.yoursway.autoupdater.installer", null);
                installer().install(productState().currentVersion(), version(), packsMap,
                        productState().rootFolder(), extInstallerFolder, productState().componentStopper());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InstallerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            double progress = state.localBytes(packs) * 1.0 / state.totalBytes(packs);
            listener().downloading(progress);
        }
    }
    
    @Override
    public boolean updating() {
        return true;
    }
    
    public ProductVersionStateMemento toMemento() {
        return ProductVersionStateMemento.newBuilder().setState(State.Installing).setVersion(
                version().toMemento()).build();
    }
    
}
