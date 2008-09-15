package com.yoursway.autoupdater.tests.internal;

import static com.google.common.collect.Sets.newHashSet;
import static com.yoursway.utils.YsFileUtils.transfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.yoursway.autoupdater.auxiliary.ComponentFile;
import com.yoursway.autoupdater.filelibrary.Request;
import com.yoursway.utils.YsDigest;

public class Pack {
    
    File packFile;
    private final Collection<ComponentFile> files = newHashSet();
    
    public Pack(File dir) throws IOException {
        //! packs only one-level folders
        
        packFile = File.createTempFile("autoupdater.pack", "");
        ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(packFile));
        for (File file : dir.listFiles()) {
            String hash = YsDigest.sha1(new FileInputStream(file));
            files.add(new ComponentFile(hash, file.length(), file.lastModified(), "-", file.getName()));
            
            zip.putNextEntry(new ZipEntry(hash));
            transfer(new FileInputStream(file), zip);
            zip.closeEntry();
            
        }
        zip.close();
        
    }
    
    public Request request() throws MalformedURLException {
        return new Request(new URL("http://localhost/pack.zip"), packFile.length(), hash());
    }
    
    public Collection<ComponentFile> files() {
        return files;
    }
    
    public String hash() {
        return "packhash";
    }
    
    public File packFile() {
        return packFile;
    }
    
}
