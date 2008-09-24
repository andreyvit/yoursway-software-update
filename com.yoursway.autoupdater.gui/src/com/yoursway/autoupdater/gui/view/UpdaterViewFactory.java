package com.yoursway.autoupdater.gui.view;

import com.yoursway.autoupdater.core.auxiliary.SuiteDefinition;
import com.yoursway.autoupdater.core.auxiliary.UpdatableApplicationView;
import com.yoursway.autoupdater.core.localrepository.LocalRepository;

public interface UpdaterViewFactory {
    
    UpdaterView createView(UpdatableApplicationView appView, SuiteDefinition suite, LocalRepository repo);
    
}
