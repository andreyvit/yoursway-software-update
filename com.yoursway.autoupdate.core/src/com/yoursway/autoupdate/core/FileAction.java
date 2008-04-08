package com.yoursway.autoupdate.core;

import java.io.File;

import com.google.common.base.Function;
import com.yoursway.autoupdate.core.actions.Action;
import com.yoursway.autoupdate.core.actions.concrete.CopyFileAction;
import com.yoursway.autoupdate.core.actions.concrete.RemoveFileAction;
import com.yoursway.autoupdate.core.versions.definitions.RemoteFile;
import com.yoursway.utils.relativepath.RelativePath;

public abstract class FileAction {
	
	private final RelativePath file;
    public static final Function<FileAction, RelativePath> ACTION_TO_PATH = new Function<FileAction, RelativePath>() {
        
        public RelativePath apply(FileAction action) {
            return action.file();
        }
        
    };

	public static final class RemoveAction extends FileAction {
		
		public RemoveAction(RelativePath file) {
			super(file);
		}

		public boolean isChanged() {
			return true;
		}

        @Override
        public Action createReal(File root, File replacement) {
            return new RemoveFileAction(file().toFile(root));
        }

        @Override
        public RemoteFile replacement() {
            return null;
        }
		
	}
	
	private static abstract class ReplaceAction extends FileAction {
		
		private final RemoteFile replaceWith;

		public ReplaceAction(RelativePath file, RemoteFile replaceWith) {
			super(file);
			this.replaceWith = replaceWith;
		}
		
		public RemoteFile replacement() {
            return replaceWith;
        }
		
		public boolean isChanged() {
			return true;
		}
		
	}
	
	public static final class AddAction extends ReplaceAction {

		public AddAction(RelativePath file, RemoteFile replaceWith) {
			super(file, replaceWith);
		}

        @Override
        public Action createReal(File root, File replacement) {
            return new CopyFileAction(replacement, file().toFile(root));
        }
		
	}
	
	public static final class UpdateAction extends ReplaceAction {
		
		public UpdateAction(RelativePath file, RemoteFile replaceWith) {
			super(file, replaceWith);
		}
        @Override
        public Action createReal(File root, File replacement) {
            return new CopyFileAction(replacement, file().toFile(root));
        }

	}
	
	public FileAction(RelativePath file) {
		this.file = file;
	}
	
	public RelativePath file() {
	    return file;
	}
	
	public abstract Action createReal(File root, File replacement);

	public abstract boolean isChanged();

	public abstract RemoteFile replacement();

}
