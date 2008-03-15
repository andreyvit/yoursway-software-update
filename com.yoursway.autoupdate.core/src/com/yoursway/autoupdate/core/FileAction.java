package com.yoursway.autoupdate.core;

import com.yoursway.autoupdate.core.path.Path;
import com.yoursway.autoupdate.core.versiondef.RemoteFile;

public abstract class FileAction {
	
	private final Path file;

	public static final class RemoveAction extends FileAction {
		
		public RemoveAction(Path file) {
			super(file);
		}

		public boolean isChanged() {
			return true;
		}
		
	}
	
	private static abstract class ReplaceAction extends FileAction {
		
		private final RemoteFile replaceWith;

		public ReplaceAction(Path file, RemoteFile replaceWith) {
			super(file);
			this.replaceWith = replaceWith;
		}
		
		public boolean isChanged() {
			return true;
		}
		
	}
	
	public static final class AddAction extends ReplaceAction {

		public AddAction(Path file, RemoteFile replaceWith) {
			super(file, replaceWith);
		}
		
	}
	
	public static final class UpdateAction extends ReplaceAction {
		
		public UpdateAction(Path file, RemoteFile replaceWith) {
			super(file, replaceWith);
		}
		
	}
	
	public FileAction(Path file) {
		this.file = file;
	}

	public abstract boolean isChanged();

}
