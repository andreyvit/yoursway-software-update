package com.yoursway.autoupdate.core.tests.layouts;

import java.io.File;
import java.io.IOException;

public interface PluginSource {
    
    String putPlugin(String id, File destinationFolder) throws IOException;

    void putJar(String id, File destinationFolder) throws IOException;
    
}
