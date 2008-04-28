/**
 * 
 */
package com.yoursway.autoupdater.fake;

import java.io.Serializable;

import com.yoursway.autoupdate.core.ProposedUpdate;
import com.yoursway.autoupdate.core.VersionDescription;
import com.yoursway.autoupdate.core.versions.Version;

public final class FakeProposedUpdate implements ProposedUpdate, Serializable {
    
    private static final long serialVersionUID = 1L;
    private final String versionName;
    private final String versionDisplayName;
    private final String changesDescription;
    
    public FakeProposedUpdate(String versionName, String versionDisplayName, String changesDescription) {
        this.versionName = versionName;
        this.versionDisplayName = versionDisplayName;
        this.changesDescription = changesDescription;
    }
    
    public String changesDescription() {
        return changesDescription;
    }
    
    public VersionDescription targetVersion() {
        return new VersionDescription() {

            public String displayName() {
                return versionDisplayName;
            }

            public Version version() {
                return new Version(versionName);
            }
            
        };
    }
    
}