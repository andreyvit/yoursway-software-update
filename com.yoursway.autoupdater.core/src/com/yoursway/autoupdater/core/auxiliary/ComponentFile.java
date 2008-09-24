package com.yoursway.autoupdater.core.auxiliary;

import static com.yoursway.autoupdater.core.protos.LocalRepositoryProtos.ComponentFileMemento.newBuilder;

import com.yoursway.autoupdater.core.protos.LocalRepositoryProtos.ComponentFileMemento;

public class ComponentFile {
    
    final String hash;
    final long size;
    final long modified;
    private final String attribs;
    final String path;
    
    public ComponentFile(String hash, long size, long modified, String attribs, String path) {
        this.hash = hash;
        this.size = size;
        this.modified = modified;
        this.attribs = attribs;
        this.path = path;
    }
    
    static ComponentFile fromMemento(ComponentFileMemento m) {
        return new ComponentFile(m.getHash(), m.getSize(), m.getModified(), m.getAttribs(), m.getPath());
    }
    
    ComponentFileMemento toMemento() {
        return newBuilder().setHash(hash).setSize(size).setModified(modified).setAttribs(attribs).setPath(
                path).build();
    }
    
    public String hash() {
        return hash;
    }
    
    public String path() {
        return path;
    }
    
    public long modified() {
        return modified;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attribs == null) ? 0 : attribs.hashCode());
        result = prime * result + ((hash == null) ? 0 : hash.hashCode());
        result = prime * result + (int) (modified ^ (modified >>> 32));
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + (int) (size ^ (size >>> 32));
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ComponentFile other = (ComponentFile) obj;
        if (attribs == null) {
            if (other.attribs != null)
                return false;
        } else if (!attribs.equals(other.attribs))
            return false;
        if (hash == null) {
            if (other.hash != null)
                return false;
        } else if (!hash.equals(other.hash))
            return false;
        if (modified != other.modified)
            return false;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        if (size != other.size)
            return false;
        return true;
    }
    
    public boolean hasExecAttribute() {
        return attribs.contains("exec");
    }
    
}
