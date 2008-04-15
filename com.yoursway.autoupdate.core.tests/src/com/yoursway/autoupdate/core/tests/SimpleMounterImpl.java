/**
 * 
 */
package com.yoursway.autoupdate.core.tests;

import static com.google.common.collect.Maps.newHashMap;
import static com.yoursway.utils.YsDigest.md5;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import com.yoursway.autoupdate.core.actions.RemoteSource;
import com.yoursway.autoupdate.core.versions.Version;
import com.yoursway.autoupdate.core.versions.definitions.RemoteFile;
import com.yoursway.autoupdate.core.versions.definitions.VersionDefinition;
import com.yoursway.utils.URLs;
import com.yoursway.utils.XmlWriter;
import com.yoursway.utils.relativepath.RelativePath;

class SimpleMounterImpl implements SimpleMounter {
    
    private final URL updateUrl;
    private final Version version;
    private final WebServer webServer;
    
    private Map<RelativePath, RemoteFile> overrides = newHashMap();
    
    public SimpleMounterImpl(URL updateUrl, Version version, WebServer webServer) {
        this.updateUrl = updateUrl;
        this.version = version;
        this.webServer = webServer;
    }
    
    public void mount(RelativePath path, String text) {
        webServer.mount(remotePathOf(version, path), text);
        
        String md5 = md5(text);
        overrides.put(path, createRemoteFile(updateUrl, version, path, md5));
    }
    
    public void mount(RelativePath path, byte[] bytes) {
        webServer.mount(remotePathOf(version, path), bytes);
        
        try {
            String md5 = md5(new ByteArrayInputStream(bytes));
            overrides.put(path, createRemoteFile(updateUrl, version, path, md5));
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
    
    public void mount(VersionDefinition def) {
        webServer.mount(version.versionString() + ".xml", versionDefinitionToString(def));
    }
    
    public Map<RelativePath, RemoteFile> overrides() {
        return overrides;
    }
    
    public static RemoteFile createRemoteFile(URL baseUrl, Version version, RelativePath path, String md5) {
        try {
            return new RemoteFile(path, md5, new RemoteSource(remoteUrlOf(baseUrl, version, path)));
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }
    
    public static URL remoteUrlOf(URL baseUrl, Version version, RelativePath path)
            throws MalformedURLException {
        return URLs.appendPath(baseUrl, remotePathOf(version, path));
    }
    
    public static String remotePathOf(Version version, RelativePath path) {
        return version.versionString() + "/" + path.toPortableString();
    }
    
    private static String versionDefinitionToString(VersionDefinition def1) {
        try {
            StringWriter sw = new StringWriter();
            XmlWriter xw = new XmlWriter(sw);
            def1.writeTo(xw);
            xw.close();
            String xml1 = sw.toString();
            return xml1;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
    
}
