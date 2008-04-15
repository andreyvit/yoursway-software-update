package com.yoursway.autoupdate.core.tests;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.yoursway.utils.YsFileUtils.readAsString;
import static com.yoursway.utils.YsFileUtils.rewriteZip;
import static com.yoursway.utils.YsFileUtils.transfer;
import static com.yoursway.utils.YsFileUtils.writeString;
import static com.yoursway.utils.relativepath.Pathes.relativePath;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import com.yoursway.autoupdate.core.ApplicationInstallation;
import com.yoursway.utils.StreamFilter;
import com.yoursway.utils.relativepath.RelativePath;

public class NewPluginVersionCreator {
    
    private static final RelativePath META_INF_MANIFEST_MF = relativePath("META-INF/MANIFEST.MF");
    
    private final ApplicationInstallation installation;
    
    private final SimpleMounter mounter;
    
    private Map<RelativePath, FileEntity> modifiableFiles = newHashMap();
    
    private Map<RelativePath, JarFileModification> jarModifications = newHashMap();
    
    public NewPluginVersionCreator(SimpleMounter mounter, ApplicationInstallation installation) {
        this.mounter = mounter;
        this.installation = installation;
    }
    
    public void execute() throws IOException {
        for (FileEntity file : modifiableFiles.values())
            file.executeModifications(mounter);
    }
    
    public void updateManifestVersion(String bundleId, String newVersion) {
        RelativePath utilsPlugin = lookupBundle(bundleId);
        forFileOrEntry(utilsPlugin, META_INF_MANIFEST_MF).addTextModification(
                new UpdateManifestVersionModification(newVersion));
    }

    private RelativePath lookupBundle(String bundleId) {
        RelativePath utilsPlugin = installation.resolveOsgiBundleAsPath(bundleId);
        if (utilsPlugin == null)
            throw new IllegalArgumentException("Cannot find bundle with id " + bundleId);
        return utilsPlugin;
    }
    
    static class UpdateManifestVersionModification implements TextModification {
        
        private final String newVersion;
        
        public UpdateManifestVersionModification(String newVersion) {
            if (newVersion == null)
                throw new NullPointerException("newVersion");
            this.newVersion = newVersion;
        }
        
        public String modify(String source) {
            return source.replaceAll("Bundle-Version: \\d+\\.\\d+\\.\\d+\\.qualifier", "Bundle-Version: "
                    + newVersion);
        }
        
    }
    
    public Modifications forFileOrEntry(RelativePath jarOrDirectoryPath, RelativePath entryPath) {
        File jarOrDirectory = installation.resolve(jarOrDirectoryPath);
        if (jarOrDirectory.isDirectory())
            return forFile(jarOrDirectoryPath.append(entryPath));
        else
            return forArchiveEntry(jarOrDirectoryPath, entryPath);
    }
    
    public Modifications forArchiveEntry(RelativePath jarPath, RelativePath entryPath) {
        JarFileModification mod = jarModifications.get(jarPath);
        if (mod == null) {
            mod = new JarFileModification();
            forFile(jarPath).addBinaryModification(mod);
            jarModifications.put(jarPath, mod);
        }
        return mod.lookupEntry(entryPath);
    }
    
    public Modifications forFile(RelativePath path) {
        FileEntity modifiable = modifiableFiles.get(path);
        if (modifiable == null) {
            modifiable = createModifiableFile(path);
            modifiableFiles.put(path, modifiable);
        }
        return modifiable.modifications();
    }
    
    private FileEntity createModifiableFile(RelativePath path) {
        File originalFile = installation.resolve(path);
        if (originalFile == null)
            throw new IllegalArgumentException("File " + path + " does not exist");
        return new FileEntity(originalFile, path);
    }
    
    interface TextModification {
        
        String modify(String source);
        
    }
    
    interface BinaryModification {
        
        void modify(InputStream in, OutputStream out) throws IOException;
        
    }
    
    interface ModificationUnit {
        
        void executeModifications(SimpleMounter mounter) throws IOException;
        
    }
    
    
    static class Modifications implements StreamFilter {
        
        private Collection<TextModification> textModifications = newArrayList();
        
        private Collection<BinaryModification> binaryModifications = newArrayList();
        
        public void addTextModification(TextModification text) {
            textModifications.add(text);
        }
        
        public void addBinaryModification(BinaryModification mod) {
            binaryModifications.add(mod);
        }
        
        public void process(InputStream in, OutputStream out) throws IOException {
            if (!textModifications.isEmpty()) {
                ByteArrayOutputStream temp = new ByteArrayOutputStream();
                executeTextual(in, temp);
                in = new ByteArrayInputStream(temp.toByteArray());
            }
            if (!binaryModifications.isEmpty()) {
                BinaryModification[] mods = binaryModifications
                        .toArray(new BinaryModification[binaryModifications.size()]);
                for (int i = 0; i < mods.length; i++) {
                    BinaryModification mod = mods[i];
                    if (i == mods.length - 1)
                        mod.modify(in, out);
                    else {
                        ByteArrayOutputStream temp = new ByteArrayOutputStream();
                        mod.modify(in, temp);
                        in = new ByteArrayInputStream(temp.toByteArray());
                    }
                }
            } else {
                transfer(in, out);
            }
        }
        
        private void executeTextual(InputStream in, OutputStream out) throws IOException {
            String text = readAsString(in);
            for (TextModification mod : textModifications)
                text = mod.modify(text);
            writeString(out, text);
        }
        
    }
    
    static class FileEntity implements ModificationUnit {
        
        private final File original;
        private final RelativePath path;
        private final Modifications modifications = new Modifications();
        
        public FileEntity(File original, RelativePath path) {
            this.original = original;
            this.path = path;
        }
        
        public void executeModifications(SimpleMounter mounter) throws IOException {
            InputStream in = new FileInputStream(original);
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                modifications.process(in, out);
                mounter.mount(path, out.toByteArray());
            } finally {
                in.close();
            }
        }
        
        public Modifications modifications() {
            return modifications;
        }
        
    }
    
    static class JarFileModification implements BinaryModification {
        
        private Map<RelativePath, Modifications> entries = newHashMap();
        
        public Modifications lookupEntry(RelativePath path) {
            Modifications entry = entries.get(path);
            if (entry == null) {
                entry = new Modifications();
                entries.put(path, entry);
            }
            return entry;
        }
        
        public void modify(InputStream in, OutputStream out) throws IOException {
            Map<String, StreamFilter> rewrites = newHashMap();
            for (Map.Entry<RelativePath, Modifications> entry : entries.entrySet())
                rewrites.put(entry.getKey().toPortableString(), entry.getValue());
            rewriteZip(in, out, rewrites);
        }
        
    }
    
}
