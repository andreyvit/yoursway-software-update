package com.yoursway.autoupdate.core;

import java.io.Serializable;

import com.yoursway.autoupdate.core.versions.definitions.VersionDefinition;

public class ProposedUpdateImpl implements ProposedUpdate, Serializable {

    private static final long serialVersionUID = 1L;
    
    final VersionDefinition currentDef;
    final VersionDefinition freshDef;

    public ProposedUpdateImpl(VersionDefinition currentDef, VersionDefinition freshDef) {
        if (currentDef == null)
            throw new NullPointerException("currentDef is null");
        if (freshDef == null)
            throw new NullPointerException("freshDef is null");
        this.currentDef = currentDef;
        this.freshDef = freshDef;
    }

    public String changesDescription() {
        return currentDef.changesDescription();
    }

    public VersionDescription targetVersion() {
        return freshDef;
    }
    
}
