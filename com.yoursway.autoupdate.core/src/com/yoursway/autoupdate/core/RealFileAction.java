package com.yoursway.autoupdate.core;

import java.io.File;

public abstract class RealFileAction {
	
	private final File file;

	public static final class RemoveAction extends RealFileAction {
		
		public RemoveAction(File file) {
			super(file);
		}

		public boolean isChanged() {
			return true;
		}
		
	}
	
	private static abstract class ReplaceAction extends RealFileAction {
		
		private final File replaceWith;

		public ReplaceAction(File file, File replaceWith) {
			super(file);
			this.replaceWith = replaceWith;
		}
		
		public boolean isChanged() {
			return true;
		}
		
	}
	
	public static final class AddAction extends ReplaceAction {

		public AddAction(File file, File replaceWith) {
			super(file, replaceWith);
		}
		
	}
	
	public static final class UpdateAction extends ReplaceAction {
		
		public UpdateAction(File file, File replaceWith) {
			super(file, replaceWith);
		}
		
	}
	
	public RealFileAction(File file) {
		this.file = file;
	}

	public abstract boolean isChanged();

}
