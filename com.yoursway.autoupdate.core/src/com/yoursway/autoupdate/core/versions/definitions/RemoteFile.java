package com.yoursway.autoupdate.core.versions.definitions;

import java.io.IOException;

import org.eclipse.core.runtime.Assert;

import com.yoursway.autoupdate.core.actions.RemoteSource;
import com.yoursway.utils.XmlWriter;
import com.yoursway.utils.relativepath.RelativePath;

public class RemoteFile extends AppFile {

    private static final long serialVersionUID = 1L;
    
    private final RemoteSource source;

    public RemoteFile(RelativePath relativePath, String md5, RemoteSource source) {
		super(relativePath, md5);
		Assert.isNotNull(source);
		this.source = source;
	}
    
    public RemoteSource source() {
        return source;
    }

    public void writeTo(XmlWriter w, UpdaterInfo updaterInfo) throws IOException {
        w.start("file");
//        <file md5="abc" path="files/a.jar" installPath="plugins/a.jar" />
        w.attr("installPath", relativePath.toPortableString());
        w.attr("md5", md5);
        source.writeTo(w);
        if (updaterInfo.mainJar().equals(relativePath))
            w.attr("role", "updater-main");
        else if (updaterInfo.files().contains(relativePath))
            w.attr("role", "updater");
        w.end();
    }

}
