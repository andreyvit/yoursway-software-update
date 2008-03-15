package com.yoursway.autoupdate.core;

import java.io.File;
import java.util.Collection;

public interface Executor {
	
	File createTemporaryDirectory();

	void restartIntoUpdater(File updaterPath, Collection<RealFileAction> actions);
	
	void update(Collection<RealFileAction> actions);
	
}
