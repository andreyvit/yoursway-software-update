package com.yoursway.autoupdater.gui.controller;

import com.yoursway.autoupdater.auxiliary.AutoupdaterException;
import com.yoursway.autoupdater.auxiliary.SuiteDefinition;
import com.yoursway.autoupdater.auxiliary.UpdatableApplication;
import com.yoursway.autoupdater.gui.view.VersionsView;
import com.yoursway.autoupdater.gui.view.VersionsViewFactory;
import com.yoursway.autoupdater.installer.external.ExternalInstaller;
import com.yoursway.autoupdater.localrepository.LocalRepository;
import com.yoursway.utils.annotations.Nullable;

public class UpdaterController {
    
    private final UpdatableApplication app;
    private final VersionsViewFactory viewFactory;
    
    public UpdaterController(UpdatableApplication app) {
        this(app, null);
    }
    
    public UpdaterController(UpdatableApplication app, @Nullable VersionsViewFactory viewFactory) {
        if (app == null)
            throw new NullPointerException("app is null");
        this.app = app;
        
        this.viewFactory = viewFactory != null ? viewFactory : VersionsView.factory();
    }
    
    public void onStart() throws AutoupdaterException {
        if (!app.inInstallingState())
            return;
        
        ExternalInstaller.afterInstall();
        app.setInstallingState(false);
    }
    
    public void updateApplication() throws AutoupdaterException {
        viewFactory.createView(app, suite(), repo()).show();
    }
    
    private SuiteDefinition suite() throws AutoupdaterException {
        return SuiteDefinition.load(app.updateSite(), app.suiteName());
    }
    
    private LocalRepository repo() throws AutoupdaterException {
        return LocalRepository.createForGUI(app);
    }
    
}
