package com.yoursway.autoupdate.core.tests.layouts;

import static com.google.common.collect.Lists.newArrayList;
import static com.yoursway.utils.YsFileUtils.addPluginIfMatches;
import static com.yoursway.utils.YsFileUtils.chooseLatestVersion;
import static com.yoursway.utils.YsFileUtils.cp_r;
import static com.yoursway.utils.YsFileUtils.findEclipsePluginJar;
import static com.yoursway.utils.YsFileUtils.urlToFileWithProtocolCheck;
import static com.yoursway.utils.YsFileUtils.zip;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.update.configurator.ConfiguratorUtils;
import org.eclipse.update.configurator.IPlatformConfiguration.ISiteEntry;

import com.yoursway.autoupdate.core.tests.DevClassPathHelper;
import com.yoursway.utils.YsFileUtils;
import com.yoursway.utils.ZipRoot;

public class CurrentPlatformSource implements PluginSource {
    
    public void putJar(String id, File destinationFolder) throws IOException {
        URL url = Platform.getInstallLocation().getURL();
        File path = urlToFileWithProtocolCheck(url);
        File latestFolderOrJar = findEclipsePluginJar(new File(path, "plugins"), id);
        if (latestFolderOrJar == null)
            throw new IllegalArgumentException("Jar " + id + " not found.");
        YsFileUtils.cp_r(latestFolderOrJar, destinationFolder);
    }
    
    public String putPlugin(String id, File destinationFolder) throws IOException {
        ISiteEntry[] sites = ConfiguratorUtils.getCurrentPlatformConfiguration().getConfiguredSites();
        Collection<File> result = newArrayList();
        for (ISiteEntry site : sites) {
            URL url = site.getURL();
            String[] plugins = site.getPlugins();
            if (url.getProtocol().equals("file")) {
                File root = new File(url.getPath());
                for (String plugin : plugins) {
                    File folderOrJar = new File(root, plugin);
                    System.out.println("Found: " + folderOrJar);
                    addPluginIfMatches(folderOrJar, id, result);
                }
            } else {
                throw new AssertionError("Non-file site found: " + url);
            }
        }
        File latestFolderOrJar = chooseLatestVersion(result);
        if (latestFolderOrJar == null)
            throw new IllegalArgumentException("Bundle " + id + " not found.");
        
        latestFolderOrJar = copyJar(id, destinationFolder, latestFolderOrJar);
        return latestFolderOrJar.getName();
    }
    
    private File copyJar(String id, File destinationFolder, File latestFolderOrJar) throws IOException {
        if (latestFolderOrJar.isDirectory()) {
            String[] devCP = DevClassPathHelper.getDevClassPath(id);
            if (devCP != null) {
                Collection<File> excluded = newArrayList();
                List<ZipRoot> roots = newArrayList();
                for (String item : devCP)
                    if (item.equals("."))
                        continue;
                    else {
                        File f = new File(latestFolderOrJar, item);
                        roots.add(new ZipRoot(f, ""));
                        excluded.add(f);
                    }
                roots.add(new ZipRoot(latestFolderOrJar, "", excluded));
                File jar = new File(destinationFolder, latestFolderOrJar.getName() + ".jar");
                zip(jar, roots);
                return jar;
            }
        }
        cp_r(latestFolderOrJar, destinationFolder);
        return latestFolderOrJar;
    }
    
}
