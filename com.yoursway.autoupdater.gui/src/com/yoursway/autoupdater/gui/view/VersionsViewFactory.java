package com.yoursway.autoupdater.gui.view;

import com.yoursway.autoupdater.auxiliary.SuiteDefinition;
import com.yoursway.autoupdater.auxiliary.UpdatableApplicationView;
import com.yoursway.autoupdater.localrepository.LocalRepository;

public interface VersionsViewFactory {
    
    VersionsView createView(UpdatableApplicationView appView, SuiteDefinition suite, LocalRepository repo);
    
}
