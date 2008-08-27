package com.yoursway.autoupdater.tests;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static com.yoursway.autoupdater.tests.internal.FileTestUtils.fileContents;
import static com.yoursway.autoupdater.tests.internal.FileTestUtils.sizeOf;
import static com.yoursway.utils.YsFileUtils.readAsString;
import static com.yoursway.utils.YsFileUtils.writeString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.After;
import org.junit.Test;

import com.yoursway.autoupdater.auxiliary.Component;
import com.yoursway.autoupdater.auxiliary.ComponentFile;
import com.yoursway.autoupdater.auxiliary.ComponentStopper;
import com.yoursway.autoupdater.auxiliary.Product;
import com.yoursway.autoupdater.auxiliary.ProductVersion;
import com.yoursway.autoupdater.filelibrary.Request;
import com.yoursway.autoupdater.installer.Installer;
import com.yoursway.autoupdater.installer.InstallerException;
import com.yoursway.autoupdater.installer.InternalInstaller;
import com.yoursway.autoupdater.installer.external.ExternalInstaller;
import com.yoursway.autoupdater.installer.external.UnexpectedMessageException;
import com.yoursway.utils.YsFileUtils;

public class InstallerTests {
    
    private final Set<Integer> packIDs = newLinkedHashSet();
    
    private final Collection<File> tempFolders = newLinkedList();
    
    @Test
    public void external() throws IOException, InstallerException, UnexpectedMessageException {
        Installer installer = new ExternalInstaller();
        Collection<Component> components = newLinkedList(component(12, 25), component(23, 42));
        File target = createTempFolder();
        
        install(installer, components, components, target);
        
        ExternalInstaller.client().reconnect();
        ExternalInstaller.client().receive("OK");
        ExternalInstaller.client().send("OK");
        
        for (int i = 12; i <= 42; i++)
            assertEquals(fileContents(sizeOf(i)), readAsString(new File(target, filepath(i))));
    }
    
    @Test
    public void internal() throws IOException, InstallerException {
        Installer installer = new InternalInstaller();
        Collection<Component> components = newLinkedList(component(12, 25), component(23, 42));
        File target = createTempFolder();
        
        install(installer, components, components, target);
        
        for (int i = 12; i <= 42; i++)
            assertEquals(fileContents(sizeOf(i)), readAsString(new File(target, filepath(i))));
    }
    
    @Test
    public void deleting() throws IOException, InstallerException {
        Installer installer = new InternalInstaller();
        Collection<Component> components1 = newLinkedList(component(8, 15), component(33, 48));
        Collection<Component> components2 = newLinkedList(component(12, 25), component(23, 42));
        File target = createTempFolder();
        
        createFiles(target, components1);
        for (int i = 8; i < 12; i++)
            assertTrue(new File(target, filepath(i)).exists());
        
        install(installer, components1, components2, target);
        
        for (int i = 8; i < 12; i++)
            assertFalse(new File(target, filepath(i)).exists());
        for (int i = 12; i <= 42; i++)
            assertEquals(fileContents(sizeOf(i)), readAsString(new File(target, filepath(i))));
    }
    
    @After
    public void cleanEach() {
        for (File folder : tempFolders)
            YsFileUtils.deleteRecursively(folder);
        
        tempFolders.clear();
    }
    
    private void install(Installer installer, Collection<Component> oldComponents,
            Collection<Component> newComponents, File target) throws IOException, InstallerException {
        
        Product product = new Product("UNNAMED");
        Collection<Request> p = newLinkedList();
        
        ProductVersion current = new ProductVersion(product, p, oldComponents, "");
        ProductVersion version = new ProductVersion(product, p, newComponents, "");
        
        Map<String, File> packs = packs();
        File extInstallerFolder = createTempFolder();
        ComponentStopper stopper = new ComponentStopper() {
            public boolean stop() {
                return true;
            }
        };
        
        installer.install(current, version, packs, target, extInstallerFolder, stopper);
    }
    
    private void createFiles(File target, Collection<Component> components) throws IOException {
        for (Component component : components)
            for (ComponentFile file : component.files())
                new File(target, file.path()).createNewFile();
    }
    
    private File createTempFolder() throws IOException {
        File folder = YsFileUtils.createTempFolder("autoupdater.installer.test", null);
        tempFolders.add(folder);
        return folder;
    }
    
    private Component component(int first, int last) throws MalformedURLException {
        return new Component(files(first, last), packs(first, last));
    }
    
    private Collection<ComponentFile> files(int first, int last) {
        Collection<ComponentFile> files = newLinkedList();
        for (int i = first; i <= last; i++)
            files.add(new ComponentFile("filehash" + i, sizeOf(i), 0, filepath(i)));
        return files;
    }
    
    private String filepath(int i) {
        return "filepath" + i;
    }
    
    private Collection<Request> packs(int first, int last) throws MalformedURLException {
        Collection<Request> packs = newLinkedList();
        for (int i = first / 10; i <= last / 10; i++) {
            packs.add(new Request(new URL("http://localhost/"), 0, "packhash" + i));
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
