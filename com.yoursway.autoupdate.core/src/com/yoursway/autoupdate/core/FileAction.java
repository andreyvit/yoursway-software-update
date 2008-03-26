package com.yoursway.autoupdate.core;

import java.io.File;

import com.google.common.base.Function;
import com.yoursway.autoupdate.core.actions.CopyFileAction;
import com.yoursway.autoupdate.core.actions.RemoveFileAction;
import com.yoursway.autoupdate.core.path.Path;
import com.yoursway.autoupdate.core.versiondef.RemoteFile;

public abstract class FileAction {
	
	private final Path file;
    public static final Function<FileAction, Path> ACTION_TO_PATH = new Function<FileAction, Path>() {
        
        public Path apply(FileAction action) {
            return action.file();
        }
        
    };

	public static final class RemoveAction extends FileAction {
		
		public RemoveAction(Path file) {
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

		public ReplaceAction(Path file, RemoteFile replaceWith) {
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

		public AddAction(Path file, RemoteFile replaceWith) {
			super(file, replaceWith);
		}

        @Override
        public Action createReal(File root, File replacement) {
            return new CopyFileAction(replacement, file().toFile(root));
        }
		
	}
	
	public static final class UpdateAction extends ReplaceAction {
		
		public UpdateAction(Path file, RemoteFile replaceWith) {
			super(file, replaceWith);
		}
        @Override
        public Action createReal(File root, File replacement) {
            return new CopyFileAction(replacement, file().toFile(root));
        }

	}
	
	public FileAction(Path file) {
		this.file = file;
	}
	
	public Path file() {
	    return file;
	}
	
	public abstract Action createReal(File root, File replacement);

	public abstract boolean isChanged();

	public abstract RemoteFile replacement();

}
