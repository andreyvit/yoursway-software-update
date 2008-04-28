package com.yoursway.autoupdate.core.versions.definitions;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.eclipse.core.runtime.Assert;

import com.yoursway.autoupdate.core.VersionDescription;
import com.yoursway.autoupdate.core.versions.Version;
import com.yoursway.utils.XmlWriter;

public class VersionDefinition implements VersionDescription {
    
    private final RemoteFile[] files;
    private final Version version;
    private final String changes;
    private final String displayName;
    private final Version nextVersion;
    private final Date date;
    private final UpdaterInfo updaterInfo;
    
    public VersionDefinition(Version version, String displayName, Version nextVersion,
            String changesDescription, Collection<RemoteFile> newer, Date date,
            UpdaterInfo updaterInfo) {
        Assert.isNotNull(version);
        Assert.isNotNull(date);
        Assert.isNotNull(displayName);
        Assert.isNotNull(newer);
        Assert.isNotNull(updaterInfo);
        this.version = version;
        this.date = date;
        this.files = newer.toArray(new RemoteFile[0]);
        this.nextVersion = nextVersion;
        this.changes = changesDescription;
        this.displayName = displayName;
        this.updaterInfo = updaterInfo;
    }
    
    public String displayName() {
        return displayName;
    }
    
    public Version version() {
        return version;
    }
    
    public Date date() {
        return date;
    }
    
    public String changesDescription() {
        return changes;
    }
    
    public Collection<RemoteFile> files() {
        return asList(files);
    }
    
    public Version nextVersion() {
        return nextVersion;
    }
    
    public boolean hasNewerVersion() {
        return nextVersion != null;
    }
    
    public UpdaterInfo updaterInfo() {
        return updaterInfo;
    }
    
    @Override
    public String toString() {
        return version + " -> " + nextVersion;
    }
    
    public void writeTo(XmlWriter w) throws IOException {
        w.start("version");
        w.tag("display-name", displayName);
        w.tag("date", createDateFormat().format(date));
        if (!hasNewerVersion()) 
            w.tag("latest", "true");
        else {
            w.tag("next-version", nextVersion.versionString());
            w.tag("changes", changes);
        }
        writeFilesTo(w);
        w.end();
    }

    private void writeFilesTo(XmlWriter w) throws IOException {
        w.start("files");
        for (RemoteFile file : files) 
            file.writeTo(w, updaterInfo);
        w.end();
    }

    public static DateFormat createDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm Z");
    }
    
}
