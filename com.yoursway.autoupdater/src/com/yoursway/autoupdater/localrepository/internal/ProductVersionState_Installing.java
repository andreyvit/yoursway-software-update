package com.yoursway.autoupdater.localrepository.internal;

import com.yoursway.autoupdater.auxiliary.Packs;
import com.yoursway.autoupdater.internal.downloader.DownloadProgress;
import com.yoursway.autoupdater.internal.downloader.DownloadProgressListener;
import com.yoursway.autoupdater.internal.downloader.Downloader;
import com.yoursway.autoupdater.internal.installer.Installer;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionStateMemento.State;

public class ProductVersionState_Installing extends AbstractProductVersionState {
    
    public ProductVersionState_Installing(ProductVersionStateWrap wrap) {
        super(wrap);
    }
    
    @Override
    public void continueWork() {
        
        Packs packs = version().packs();
        final DownloadProgress progress = Downloader.instance().startDownloading(packs);
        
        progress.events().addListener(new DownloadProgressListener() {
            
            public void completed() {
                if (progress.successful()) {
                    
                    Installer installer = new Installer(version());
                    
                    if (installer.restartRequired()) {
                        //> make installation script
                        //> run installation script
                        //> wait for a signal
                        //> quit
                    } else {
                        //> prepare components
                        installer.install();
                        //> postpare components
                    }
                    
                    //> check if it installed successfully
                    
                    ProductVersionStateWrap current = productState().currentVersionStateOrNull();
                    if (current != null)
                        current.changeState(new ProductVersionState_Old(current));
                    
                    changeState(new ProductVersionState_Old(wrap));
                    
                } else {
                    //> repeat
                }
            }
            
            public void progressChanged() {
                //>
            }
            
        });
        
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
