package com.yoursway.autoupdater.internal;

import com.yoursway.autoupdater.Downloader;
import com.yoursway.autoupdater.auxiliary.Packs;

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
        new Downloader(null).download(packs); //!
        
        //> check if packs downloaded successfully
        
        changeState(new ProductVersionState_Installing(wrap));
    }
    
}
