package com.yoursway.autoupdater.auxiliary;

import static com.google.common.collect.Sets.newHashSet;
import static com.yoursway.autoupdater.protos.LocalRepositoryProtos.ComponentFileMemento.newBuilder;

import java.util.Set;

import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ComponentFileMemento;

public class ComponentFile {
    
    final String hash;
    final long size;
    final long modified;
    private final String attribs;
    final String path;
    
    private final Set<String> tags;
    
    public ComponentFile(String hash, long size, long modified, String attribs, String[] tags, String path) {
        this.hash = hash;
        this.size = size;
        this.modified = modified;
        this.attribs = attribs;
        this.path = path;
        
        this.tags = newHashSet(tags);
    }
    
    static ComponentFile fromMemento(ComponentFileMemento m) {
        String[] tags = m.getTagList().toArray(new String[m.getTagCount()]);
        return new ComponentFile(m.getHash(), m.getSize(), m.getModified(), m.getAttribs(), tags, m.getPath());
    }
    
    ComponentFileMemento toMemento() {
        return newBuilder().setHash(hash).setSize(size).setModified(modified).setAttribs(attribs).setPath(
                path).addAllTag(tags).build();
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
        result = prime * result + ((tags == null) ? 0 : tags.hashCode());
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
        if (tags == null) {
            if (other.tags != null)
                return false;
        } else if (!tags.equals(other.tags))
            return false;
        return true;
    }
    
    private boolean hasTag(String tag) {
        return tags.contains(tag);
    }
    
    public boolean isAppExec() {
        return hasTag("exec");
    }
    
    public boolean isRunJar() {
        return hasTag("runjar");
    }
    
    public boolean hasExecAttribute() {
        return attribs.contains("exec");
    }
    
}
