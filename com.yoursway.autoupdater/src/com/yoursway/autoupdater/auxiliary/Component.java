package com.yoursway.autoupdater.auxiliary;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.Map;

import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ComponentFileMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ComponentMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ComponentMemento.Builder;

public class Component {
    
    private final Map<String, ComponentFile> files;
    private final Collection<String> packs;
    
    public Component(Map<String, ComponentFile> files, Collection<String> packs) {
        this.files = files;
        this.packs = packs;
    }
    
    public Iterable<ComponentFile> files() {
        return files.values();
    }
    
    static Component fromMemento(ComponentMemento memento) {
        Map<String, ComponentFile> files = newHashMap();
        for (ComponentFileMemento m : memento.getFileList()) {
            ComponentFile file = ComponentFile.fromMemento(m);
            files.put(file.hash, file);
        }
        Collection<String> packs = memento.getPackList();
        return new Component(files, packs);
    }
    
    ComponentMemento toMemento() {
        Builder b = ComponentMemento.newBuilder().addAllPack(packs);
        for (ComponentFile file : files.values())
            b.addFile(file.toMemento());
        return b.build();
    }
    
    public Iterable<String> packs() {
        return packs;
    }
    
}
