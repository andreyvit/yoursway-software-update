package com.yoursway.autoupdater.gui.view;

import com.yoursway.autoupdater.auxiliary.SuiteDefinition;
import com.yoursway.autoupdater.auxiliary.UpdatableApplication;
import com.yoursway.autoupdater.localrepository.LocalRepository;

public interface VersionsViewFactory {
    
    VersionsView createView(UpdatableApplication app, SuiteDefinition suite, LocalRepository repo);
    
}
