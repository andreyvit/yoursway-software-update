package com.yoursway.autoupdater.auxiliary;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.Map;

import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ComponentFileMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ComponentMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ComponentMemento.Builder;

public class Component {
    
    private final Map<String, ComponentFile> files = newHashMap();
    private final Collection<String> packs;
    
    public Component(Collection<ComponentFile> files, Collection<String> packs) {
        if (packs == null)
            throw new NullPointerException("packs is null");
        
        for (ComponentFile file : files)
            this.files.put(file.hash, file);
        
        this.packs = packs;
    }
    
    public Iterable<ComponentFile> files() {
        return files.values();
    }
    
    static Component fromMemento(ComponentMemento memento) {
        Collection<ComponentFile> files = newLinkedList();
        for (ComponentFileMemento m : memento.getFileList())
            files.add(ComponentFile.fromMemento(m));
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
