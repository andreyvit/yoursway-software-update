package com.yoursway.autoupdater.auxiliary;

import static com.yoursway.autoupdater.protos.LocalRepositoryProtos.ComponentFileMemento.newBuilder;

import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ComponentFileMemento;

public class ComponentFile {
    
    final String hash;
    final long size;
    final long modified;
    final String path;
    
    public ComponentFile(String hash, long size, long modified, String path) {
        this.hash = hash;
        this.size = size;
        this.modified = modified;
        this.path = path;
    }
    
    static ComponentFile fromMemento(ComponentFileMemento m) {
        return new ComponentFile(m.getHash(), m.getSize(), m.getModified(), m.getPath());
    }
    
    ComponentFileMemento toMemento() {
        return newBuilder().setHash(hash).setSize(size).setModified(modified).setPath(path).build();
    }
    
    public String hash() {
        return hash;
    }
    
    public String path() {
        return path;
    }
    
}
