package com.yoursway.autoupdate.core;

import com.yoursway.autoupdate.core.versiondef.RemoteFile;

public abstract class FileAction {
	
	private static final class KeepAction extends FileAction {
		
		public boolean isChanged() {
			return false;
		}
		
	}
	
	private static final class RemoveAction extends FileAction {
		
		public boolean isChanged() {
			return true;
		}
		
	}
	
	private static abstract class ReplaceAction extends FileAction {
		
		private final RemoteFile replaceWith;

		public ReplaceAction(RemoteFile replaceWith) {
			this.replaceWith = replaceWith;
		}
		
		public boolean isChanged() {
			return true;
		}
		
	}
	
	public static final class AddAction extends ReplaceAction {

		public AddAction(RemoteFile replaceWith) {
			super(replaceWith);
		}
		
	}
	
	public static final class UpdateAction extends ReplaceAction {
		
		public UpdateAction(RemoteFile replaceWith) {
			super(replaceWith);
		}
		
	}

	public static final FileAction REMOVE = new RemoveAction();
	
	public static final FileAction KEEP = new KeepAction();
	
	public abstract boolean isChanged();

}
