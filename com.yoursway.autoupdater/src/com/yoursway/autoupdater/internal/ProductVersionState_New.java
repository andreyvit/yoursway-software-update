package com.yoursway.autoupdater.internal;

public class ProductVersionState_New extends AbstractProductVersionState {
    
    public ProductVersionState_New(ProductVersionStateWrap wrap) {
        super(wrap);
    }
    
    @Override
    public void startUpdating() {
        changeState(new ProductVersionState_Downloading(wrap));
    }
    
}
