/**
 * 
 */
package com.yoursway.autoupdate.core.tests;

import java.util.Map;

import com.yoursway.autoupdate.core.versions.definitions.RemoteFile;
import com.yoursway.autoupdate.core.versions.definitions.VersionDefinition;
import com.yoursway.utils.relativepath.RelativePath;

interface SimpleMounter {
    
    void mount(RelativePath path, String text);
    
    void mount(RelativePath path, byte[] bytes);
    
    void mount(VersionDefinition def);
    
    Map<RelativePath, RemoteFile> overrides();

}
