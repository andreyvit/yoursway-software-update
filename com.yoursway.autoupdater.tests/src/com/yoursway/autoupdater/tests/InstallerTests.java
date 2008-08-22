package com.yoursway.autoupdater.tests;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static com.yoursway.autoupdater.tests.internal.FileTestUtils.fileContents;
import static com.yoursway.autoupdater.tests.internal.FileTestUtils.sizeOf;
import static com.yoursway.utils.YsFileUtils.writeString;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Test;

import com.yoursway.autoupdater.auxiliary.Component;
import com.yoursway.autoupdater.auxiliary.ComponentFile;
import com.yoursway.autoupdater.auxiliary.ComponentStopper;
import com.yoursway.autoupdater.auxiliary.Product;
import com.yoursway.autoupdater.auxiliary.ProductVersion;
import com.yoursway.autoupdater.filelibrary.Request;
import com.yoursway.autoupdater.installer.Installer;
import com.yoursway.autoupdater.installer.InstallerException;
import com.yoursway.autoupdater.installer.InstallerImpl;
import com.yoursway.utils.YsFileUtils;

public class InstallerTests {
    
    private final Set<Integer> packIDs = newLinkedHashSet();
    
    @Test
    public void install() throws IOException, InstallerException {
        
        Installer installer = new InstallerImpl();
        
        Product product = new Product();
        Collection<Request> p = newLinkedList();
        Collection<Component> components = components();
        
        ProductVersion current = new ProductVersion(product, p, components);
        ProductVersion version = new ProductVersion(product, p, components);
        Map<String, File> packs = packs();
        File target = createTempFolder();
        File extInstallerFolder = createTempFolder();
        ComponentStopper stopper = new ComponentStopper() {
            public boolean stop() {
                return true;
            }
        };
        
        installer.install(current, version, packs, target, extInstallerFolder, stopper);
    }
    
    private File createTempFolder() throws IOException {
        return YsFileUtils.createTempFolder("autoupdater.installer.test", null);
    }
    
    private LinkedList<Component> components() {
        return newLinkedList(component(12, 25), component(23, 42));
    }
    
    private Component component(int first, int last) {
        return new Component(files(first, last), packs(first, last));
    }
    
    private Collection<ComponentFile> files(int first, int last) {
        Collection<ComponentFile> files = newLinkedList();
        for (int i = first; i <= last; i++)
            files.add(new ComponentFile("filehash" + i, sizeOf(i), 0, "filepath" + i));
        return files;
    }
    
    private Collection<String> packs(int first, int last) {
        Collection<String> packs = newLinkedList();
        for (int i = first / 10; i <= last / 10; i++) {
            packs.add("packhash" + i);
            packIDs.add(i);
        }
        return packs;
    }
    
    private Map<String, File> packs() throws IOException {
        File folder = createTempFolder();
        Map<String, File> packs = newHashMap();
        for (int i : packIDs) {
            File pack = new File(folder, "packfile" + i + ".zip");
            ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(pack));
            for (int j = i * 10; j <= i * 10 + 14; j++) {
                zip.putNextEntry(new ZipEntry("filehash" + j));
                writeString(zip, fileContents(sizeOf(j)));
                zip.closeEntry();
            }
            zip.close();
            packs.put("packhash" + i, pack);
        }
        return packs;
    }
}
