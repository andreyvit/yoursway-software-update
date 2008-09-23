package com.yoursway.autoupdater.eclipse;

import static com.yoursway.utils.YsFileUtils.urlToFileWithProtocolCheck;
import static com.yoursway.utils.os.YsOSUtils.isMacOSX;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;

import com.yoursway.autoupdater.auxiliary.UpdatableApplication;
import com.yoursway.autoupdater.auxiliary.UpdatableApplicationProductFeatures;
import com.yoursway.autoupdater.auxiliary.UpdatableApplicationView;

public class UpdatableEclipse implements UpdatableApplication {
    
    public File localRepositoryPlace() throws IOException {
        return new File(rootFolder(), updatesPath() + "localRepo");
    }
    
    public String suiteName() {
        return "eclipse";
    }
    
    public String updateSite() {
        String substitution = System.getenv("YS_UPDATE_SITE");
        if (substitution != null && substitution.length() > 0)
            return substitution;
        
        return "http://updates.yoursway.com/";
    }
    
    public UpdatableApplicationView view() {
        return new UpdatableEclipseView();
    }
    
    public UpdatableApplicationProductFeatures getFeatures(String productName) {
        if (!productName.equals("eclipse"))
            throw new IllegalArgumentException("Unknown product");
        
        return new UpdatableEclipseProductFeatures();
    }
    
    static File rootFolder() {
        Location location = Platform.getInstallLocation();
        if (location != null) {
            URL url = location.getURL();
            return urlToFileWithProtocolCheck(url);
        } else {
            String path = System.getProperty("user.dir");
            File dir = new File(path);
            if (isMacOSX())
                while (!dir.getName().endsWith(".app"))
                    dir = dir.getParentFile();
            return dir;
        }
    }
    
    static String updatesPath() {
        String path = "updates/";
        try {
            if (rootFolder().getCanonicalPath().contains(".app"))
                path = "Contents/Resources/" + path;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return path;
    }
}
