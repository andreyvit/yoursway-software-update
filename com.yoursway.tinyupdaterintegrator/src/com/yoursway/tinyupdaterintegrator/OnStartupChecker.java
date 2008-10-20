package com.yoursway.tinyupdaterintegrator;

import org.eclipse.ui.IStartup;

import com.yoursway.tinyupdater.TinyUpdater;

public class OnStartupChecker implements IStartup {

	public void earlyStartup() {
		TinyUpdater.instance().checkUpdate(false);
	}

}
