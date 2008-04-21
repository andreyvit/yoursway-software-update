package com.yoursway.autoupdate.core.tests;

import static com.google.common.base.Join.join;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.yoursway.utils.YsStrings.sortedCopy;
import static com.yoursway.utils.YsStrings.toStringList;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Collection;
import java.util.Map;

import org.eclipse.update.configurator.ConfiguratorUtils;
import org.eclipse.update.configurator.IPlatformConfiguration;
import org.eclipse.update.configurator.IPlatformConfiguration.ISiteEntry;
import org.junit.Ignore;
import org.junit.Test;
import org.osgi.framework.Bundle;

import com.yoursway.autoupdate.core.ApplicationInstallation;
import com.yoursway.autoupdate.core.AutomaticUpdater;
import com.yoursway.autoupdate.core.tests.internal.Activator;
import com.yoursway.autoupdate.core.tests.layouts.CurrentPlatformSource;
import com.yoursway.autoupdate.core.tests.layouts.WritableMacBundleLayout;
import com.yoursway.autoupdate.core.versions.Version;
import com.yoursway.autoupdate.core.versions.definitions.AppFile;
import com.yoursway.autoupdate.core.versions.definitions.RemoteFile;
import com.yoursway.autoupdate.core.versions.definitions.UpdaterInfo;
import com.yoursway.autoupdate.core.versions.definitions.VersionDefinition;
import com.yoursway.autoupdate.core.versions.definitions.VersionDefinitionParser;
import com.yoursway.utils.URLs;
import com.yoursway.utils.YsFileUtils;
import com.yoursway.utils.fileset.FileSet;
import com.yoursway.utils.filespec.ConcreteFilesSpec;
import com.yoursway.utils.filespec.ExcludedFileSpec;
import com.yoursway.utils.relativepath.RelativePath;

public class IntegrationTests {
    
    @Test
//    @Ignore
    public void aaa() throws Exception {
        ApplicationInstallation install = new ApplicationInstallation(
                new File(
                        "/Users/andreyvit/Documents/junit-workspace/.metadata/.plugins/com.yoursway.autoupdate.core.tests/Fake.app/Contents/Resources/Java"));
        AutomaticUpdater.checkForUpdates(install, new Version("1.0.0.qualifier"), new URL("file:///tmp/foo/"));
    }
    
    @Test
//    @Ignore
    public void fooChanged() throws IOException, InterruptedException, ParseException {
        
        Bundle[] bundles = Activator.getContext().getBundles();
        for (Bundle b : bundles) {
            String location = b.getLocation();
            System.out.println("Bundle " + b.getSymbolicName() + " at " + location);
            
        }
        
        IPlatformConfiguration config = ConfiguratorUtils.getCurrentPlatformConfiguration();
        ISiteEntry[] sites = config.getConfiguredSites();
        for (ISiteEntry site : sites) {
            System.out.println(site);
        }
        
        WebServer webServer = new WebServer();
        URL updateUrl = new URL("http://localhost:" + webServer.getPort() + "/");
        
        try {
            File appRoot = new File(Activator.getDefault().getStateLocation().toFile(), "Fake.app");
            YsFileUtils.deleteRecursively(appRoot);
            
            WritableMacBundleLayout lll = new WritableMacBundleLayout(appRoot, new CurrentPlatformSource());
            lll.overrideUpdateUrl(updateUrl);
            lll.enableTestsPingback(URLs.appendPath(updateUrl, "/update-done"));
            
            // platform
            lll.copyPlugin("javax.servlet");
            lll.copyPlugin("org.eclipse.core.contenttype");
            lll.copyPlugin("org.eclipse.core.jobs");
            lll.copyPlugin("org.eclipse.core.runtime");
            lll.copyPlugin("org.eclipse.equinox.app");
            lll.copyPlugin("org.eclipse.equinox.common");
            lll.copyPlugin("org.eclipse.equinox.preferences");
            lll.copyPlugin("org.eclipse.equinox.registry");
            lll.copyPlugin("org.eclipse.osgi");
            lll.copyPlugin("org.eclipse.osgi.services");
            lll.copyPlugin("org.eclipse.update.configurator");
            lll.copyJar("org.eclipse.equinox.launcher");
            lll.copyJar("org.eclipse.equinox.launcher.carbon.macosx");
            // app
            lll.copyPlugin("com.google.collections");
            lll.copyPlugin("com.yoursway.autoupdate.core");
            lll.copyPlugin("com.yoursway.autoupdate.core.actions");
            RelativePath extUpdaterJar = lll.copyPlugin("com.yoursway.autoupdate.core.extupdater");
            RelativePath fakeAppPlugin = lll.copyPlugin("com.yoursway.autoupdater.core.tests.fakeapp");
            RelativePath utilsPlugin = lll.copyPlugin("com.yoursway.utils");
            
            Version currentVersion = new Version("1.0.0.qualifier");
            Version nextVersion = new Version("1.1.0.qualifier");
            
            ApplicationInstallation install = lll.toInstallation();
            FileSet allFiles = install.getFileContainer().allFiles();
            mountVersionOneDefinition(webServer, updateUrl, install, allFiles, currentVersion, nextVersion,
                    extUpdaterJar, fakeAppPlugin);
            
            Collection<RemoteFile> correctFiles = mountVersionTwo(webServer, updateUrl, nextVersion, install,
                    allFiles, extUpdaterJar, fakeAppPlugin, utilsPlugin);
            
            install.launchAndWait();
            
            //            install.launchAndWait();
            
            long end = System.currentTimeMillis() + 10000;
            boolean timeout = false;
            while (!webServer.requestDetected("update-done")) {
                if (System.currentTimeMillis() > end) {
                    timeout = true;
                    break;
                }
                Thread.sleep(200);
            }
            
            ApplicationInstallation installation = lll.toInstallation();
            installation.getFileContainer().allFiles();
            Collection<RemoteFile> realFiles = createRemoteFiles(installation, allFiles, updateUrl,
                    nextVersion);
            
            String expected = join("\n", sortedCopy(toStringList(correctFiles)));
            String actual = (timeout ? "TIMEOUT\n" : "") + join("\n", sortedCopy(toStringList(realFiles)));
            assertEquals(expected, actual);
        } finally {
            webServer.dispose();
        }
        
    }
    
    private Collection<RemoteFile> mountVersionTwo(WebServer webServer, URL updateUrl, Version nextVersion,
            ApplicationInstallation install, FileSet allFiles, RelativePath extUpdaterJar,
            RelativePath fakeAppPlugin, RelativePath utilsPlugin) throws MalformedURLException,
            ParseException, IOException {
        SimpleMounter mounter = new SimpleMounterImpl(updateUrl, nextVersion, webServer);
        NewPluginVersionCreator creator = new NewPluginVersionCreator(mounter, install);
        creator.updateManifestVersion("com.yoursway.utils", "42.1.2.3.qualifier");
        creator.updateManifestVersion("com.yoursway.autoupdater.core.tests.fakeapp", "1.1.0.qualifier");
        creator.execute();
        
        Collection<RemoteFile> files = createRemoteFiles(install, allFiles, updateUrl, nextVersion, mounter
                .overrides());
        
        FileSet updaterFiles = calculateUpdaterFiles(allFiles, fakeAppPlugin);
        
        VersionDefinition def2 = new VersionDefinition(nextVersion, "R1.1", null, null, files,
                VersionDefinitionParser.parseDate("2008-01-20 22:53 +0600"), new UpdaterInfo(
                        new ConcreteFilesSpec(updaterFiles.asCollection()), extUpdaterJar));
        mounter.mount(def2);
        return files;
    }
    
    private Collection<RemoteFile> mountVersionOneDefinition(WebServer webServer, URL updateUrl,
            ApplicationInstallation install, FileSet allFiles, Version currentVersion, Version nextVersion,
            RelativePath extUpdaterJar, RelativePath fakeAppPlugin) throws MalformedURLException,
            ParseException, IOException {
        SimpleMounter mounter = new SimpleMounterImpl(updateUrl, currentVersion, webServer);
        Collection<RemoteFile> files = createRemoteFiles(install, allFiles, updateUrl, currentVersion);
        
        FileSet updaterFiles = calculateUpdaterFiles(allFiles, fakeAppPlugin);
        
        VersionDefinition def1 = new VersionDefinition(currentVersion, "R1.0", nextVersion,
                "Everything changed", files, VersionDefinitionParser.parseDate("2008-01-18 21:43 +0600"),
                new UpdaterInfo(new ConcreteFilesSpec(updaterFiles.asCollection()), extUpdaterJar));
        
        mounter.mount(def1);
        return files;
    }
    
    private FileSet calculateUpdaterFiles(FileSet allFiles, RelativePath fakeAppPlugin) {
        ExcludedFileSpec updaterSpec = new ExcludedFileSpec(new ConcreteFilesSpec(allFiles.asCollection()),
                new ConcreteFilesSpec(newArrayList(fakeAppPlugin)));
        FileSet updaterFiles = updaterSpec.resolve(allFiles);
        return updaterFiles;
    }
    
    private Collection<RemoteFile> createRemoteFiles(ApplicationInstallation install, FileSet allFiles,
            URL baseUrl, Version version) throws MalformedURLException {
        Map<RelativePath, RemoteFile> overrides = newHashMap();
        return createRemoteFiles(install, allFiles, baseUrl, version, overrides);
    }
    
    private Collection<RemoteFile> createRemoteFiles(ApplicationInstallation install, FileSet allFiles,
            URL baseUrl, Version version, Map<RelativePath, RemoteFile> overrides)
            throws MalformedURLException {
        Collection<RemoteFile> files = newArrayList();
        for (RelativePath path : allFiles.asCollection()) {
            RemoteFile remote = overrides.get(path);
            if (remote == null) {
                AppFile file = install.getFileContainer().resolve(path);
                remote = SimpleMounterImpl.createRemoteFile(baseUrl, version, path, file.md5());
            }
            files.add(remote);
        }
        return files;
    }
    
}
