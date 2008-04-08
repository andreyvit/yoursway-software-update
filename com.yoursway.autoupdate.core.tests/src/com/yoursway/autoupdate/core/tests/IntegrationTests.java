package com.yoursway.autoupdate.core.tests;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.junit.Test;

import com.yoursway.autoupdate.core.ApplicationInstallation;
import com.yoursway.autoupdate.core.AutomaticUpdater;
import com.yoursway.autoupdate.core.UpdatesFoundExit;
import com.yoursway.autoupdate.core.app.layout.PlatformLayout;
import com.yoursway.autoupdate.core.tests.internal.SimpleHttpServer;
import com.yoursway.autoupdate.core.tests.internal.SimpleServlet;
import com.yoursway.autoupdate.core.versions.Version;
import com.yoursway.utils.YsFileUtils;

public class IntegrationTests {
    
    private final static int PORT = 8744;
    
    @SuppressWarnings("deprecation")
    @Test
    public void fooChanged() throws IOException {
        SimpleServlet servlet = new SimpleServlet() {
            
            public void log(String s2) {
                System.out.println(s2);
            }
            
            public InputStream openFile(String path) throws IOException {
                return Activator.openResource("tests/integration/" + path);
            }
            
        };
        SimpleHttpServer server = new SimpleHttpServer(PORT, servlet);
        try {
            File root = YsFileUtils.createTempFolder("autoupdater", "tests");
            
            File platformLocation = new File(Platform.getInstallLocation().getURL().getPath());
            
            PlatformLayout layout = ApplicationInstallation.determineLayout(platformLocation);
            
            try {
                AutomaticUpdater.checkForUpdates(new URL("http://localhost:" + PORT + "/"),
                        new Version("1.0"));
                fail("No updates found.");
            } catch (UpdatesFoundExit e) {
            }
            
            layout.createRunnableUpdaterAt(root);
            
            System.out.println(platformLocation);
        } finally {
            server.stop();
        }
        
    }
    
}
