package com.yoursway.autoupdater.localrepository.internal;

import com.yoursway.autoupdater.auxiliary.Packs;
import com.yoursway.autoupdater.internal.downloader.DownloadProgress;
import com.yoursway.autoupdater.internal.downloader.DownloadProgressListener;
import com.yoursway.autoupdater.internal.downloader.Downloader;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento.State;

public class ProductVersionState_Downloading extends AbstractProductVersionState {
    
    public ProductVersionState_Downloading(ProductVersionStateWrap wrap) {
        super(wrap);
    }
    
    @Override
    public boolean updating() {
        return true;
    }
    
    @Override
    public void continueWork() {
        Packs packs = version().packs();
        final DownloadProgress progress = Downloader.instance().startDownloading(packs);
        
        progress.events().addListener(new DownloadProgressListener() {
            
            public void completed() {
                if (progress.successful())
                    changeState(new ProductVersionState_Installing(wrap));
                else {
                    //> repeat
                }
            }
            
            public void progressChanged() {
                //>
            }
            
        });
        
    }
    
    public ProductVersionStateMemento toMemento() {
        return ProductVersionStateMemento.newBuilder().setState(State.Downloading).setVersion(
                version().toMemento()).build();
    }
    
}
