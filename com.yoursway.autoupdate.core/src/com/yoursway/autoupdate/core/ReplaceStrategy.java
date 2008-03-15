package com.yoursway.autoupdate.core;

public enum ReplaceStrategy {
	
	HOT_REPLACE(false) {
		
		public UpdaterFileHandling updaterHandlingStrategy() {
			return UpdaterFileHandling.REPLACE_INPLACE;
		}
		
	},
	
//	REPLACE_THEN_RESTART(true) {
//		
//		public UpdaterFileHandling updaterHandlingStrategy() {
//			return UpdaterFileHandling.REPLACE_INPLACE;
//		}
//		
//	},
	
	REPLACE_AFTER_SHUTDOWN(true) {
		
		public UpdaterFileHandling updaterHandlingStrategy() {
			return UpdaterFileHandling.MAKE_COPY;
		}
		
	};
	
	private final boolean needsUpdater;

	private ReplaceStrategy(boolean needsUpdater) {
		this.needsUpdater = needsUpdater;
	}
	
	public boolean needsUpdater() {
		return needsUpdater;
	}
	
	public abstract UpdaterFileHandling updaterHandlingStrategy();
	
	public ReplaceStrategy worst(ReplaceStrategy another) {
		if (compareTo(another) > 0)
			return this;
		else
			return another;
	}

}
