package com.yoursway.autoupdate.core.versions.definitions;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.eclipse.core.runtime.Assert;

import com.yoursway.autoupdate.core.versions.Version;

public class UrlBasedVersionDefinitionLoader implements IVersionDefinitionLoader {
    
    private final URL repositoryURL;
    
    public UrlBasedVersionDefinitionLoader(URL repositoryURL) {
        Assert.isNotNull(repositoryURL);
        this.repositoryURL = repositoryURL;
        
    }
    
    protected InputStream contentsFor(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        connection.connect();
        if (connection instanceof HttpURLConnection) {
            HttpURLConnection http = (HttpURLConnection) connection;
            if (http.getResponseCode() != HttpURLConnection.HTTP_OK)
                throw new IOException("Response is " + http.getResponseCode() + " "
                        + http.getResponseMessage());
        }
        return connection.getInputStream();
    }
    
    public VersionDefinition loadDefinition(Version currentVersion) throws VersionDefinitionNotAvailable, InvalidVersionDefinitionException {
        try {
            InputStream stream = open(currentVersion);
            return new VersionDefinitionParser().parse(repositoryURL, currentVersion, stream);
        } catch (IOException e) {
            throw new VersionDefinitionNotAvailable(e);
        }
    }
    
    private InputStream open(Version currentVersion) throws AssertionError, IOException {
        URL updateFileURL;
        try {
            updateFileURL = new URL(repositoryURL, URLEncoder.encode(currentVersion.versionString() + ".xml",
                    "UTF-8"));
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
        InputStream stream = contentsFor(updateFileURL);
        return stream;
    }
    
}
