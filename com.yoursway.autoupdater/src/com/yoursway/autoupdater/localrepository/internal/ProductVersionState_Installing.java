package com.yoursway.autoupdater.localrepository.internal;

import static com.google.common.collect.Maps.newHashMap;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import com.yoursway.autoupdater.auxiliary.AutoupdaterException;
import com.yoursway.autoupdater.auxiliary.ComponentStopper;
import com.yoursway.autoupdater.filelibrary.FileLibraryListener;
import com.yoursway.autoupdater.filelibrary.LibraryState;
import com.yoursway.autoupdater.filelibrary.Request;
import com.yoursway.autoupdater.installer.Installation;
import com.yoursway.autoupdater.installer.InstallerException;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.LocalProductVersionMemento.State;
import com.yoursway.utils.log.Log;
import com.yoursway.utils.log.LogEntryType;

final class ProductVersionState_Installing extends AbstractProductVersionState implements FileLibraryListener {
    
    ProductVersionState_Installing(LocalProductVersion version) {
        super(version);
    }
    
    @Override
    public void continueWork() {
        Log.write("Ordering files.");
        fire().downloadingStarted();
        orderManager().orderChanged();
    }
    
    @Override
    public Collection<Request> libraryRequests() {
        return versionDefinition().packRequests();
    }
    
    @Override
    public void libraryChanged(LibraryState state) {
        try {
            Collection<Request> packRequests = versionDefinition().packRequests();
            if (state.filesReady(packRequests)) {
                Log.write("Files ready.");
                fire().downloadingCompleted();
                
                Collection<File> localFiles = null;
                try {
                    localFiles = state.getLocalFiles(packRequests);
                } catch (Throwable e) {
                    throw new InvalidDownloadedFilesException(e);
                }
                startInstallation(localFiles);
            } else {
                Log.write(state.localBytes(packRequests) + " of " + state.totalBytes(packRequests));
                double progress = state.localBytes(packRequests) * 1.0 / state.totalBytes(packRequests);
                fire().downloading(progress);
            }
        } catch (Throwable e) {
            Log.write(e.getClass().getSimpleName() + ": " + e.getMessage(), LogEntryType.ERROR);
            AutoupdaterException ae = e instanceof AutoupdaterException ? (AutoupdaterException) e
                    : new AutoupdaterException(e);
            
            errorOccured(ae);
            changeState(new ProductVersionState_InternalError(version));
        }
    }
    
    private void startInstallation(Collection<File> localPacks) throws AssertionError {
        Map<String, File> packsMap = newHashMap();
        for (File file : localPacks) {
            String name = file.getName();
            if (!name.endsWith(".zip"))
                throw new AssertionError("A pack file name must ends with .zip");
            String hash = name.substring(0, name.length() - 4);
            packsMap.put(hash, file);
        }
        
        try {
            Installation installation = new Installation(version, packsMap);
            
            ComponentStopper stopper = new ComponentStopper() {
                public boolean stop() {
                    changeState(new ProductVersionState_InstallingExternal(version));
                    return componentStopper().stop();
                }
            };
            installer().install(installation, stopper);
            
            // an application wasn't terminated by the a stopper
            changeState(new ProductVersionState_InternalError(version));
            
        } catch (InstallerException e) {
            e.printStackTrace(); //!
            errorOccured(e);
            changeState(new ProductVersionState_InternalError(version));
        }
    }
    
    @Override
    public boolean updating() {
        return true;
    }
    
    public State toMementoState() {
        return State.Installing;
    }
    
}
