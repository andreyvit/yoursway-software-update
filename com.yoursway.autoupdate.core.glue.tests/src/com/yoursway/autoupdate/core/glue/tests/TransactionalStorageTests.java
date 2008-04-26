package com.yoursway.autoupdate.core.glue.tests;

import static com.yoursway.utils.YsFileUtils.deleteRecursively;
import static com.yoursway.utils.YsFileUtils.readAsString;
import static com.yoursway.utils.YsFileUtils.readAsStringAndClose;
import static com.yoursway.utils.YsFileUtils.writeString;
import static com.yoursway.utils.YsFileUtils.writeStringAndClose;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;

import com.yoursway.autoupdate.core.glue.persister.TransactionalStorage;
import com.yoursway.utils.DelegatingOutputStream;

public class TransactionalStorageTests {
    
    private File mainFile;
    private File logFile;
    private TransactionalStorage storage;
    
    @Before
    public void setUp() {
        File stateLocation = new File(Activator.getDefault().getStateLocation().toFile(), getClass()
                .getSimpleName());
        deleteRecursively(stateLocation);
        stateLocation.mkdirs();
        mainFile = new File(stateLocation, "main");
        logFile = new File(stateLocation, "log");
    }
    
    @Test
    public void setUpWorks() {
        assertFalse(mainFile.exists());
        assertFalse(logFile.exists());
    }
    
    @Test
    public void emptyStorageRead() throws IOException {
        storage = new TransactionalStorage(mainFile, logFile);
        assertEquals("", readAsStringAndClose(storage.openRead()));
    }
    
    @Test
    public void emptyStorageWrite() throws IOException {
        storage = new TransactionalStorage(mainFile, logFile);
        writeStringAndClose(storage.openWrite(), "Foo");
        assertEquals("Foo", readAsString(mainFile));
        assertEquals(false, logFile.exists());
    }
    
    @Test
    public void emptyStorageWriteUsesMarkerFile() throws IOException {
        storage = new TransactionalStorage(mainFile, logFile);
        OutputStream out = storage.openWrite();
        assertTrue(mainFile.exists());
        assertEquals("", readAsString(mainFile));
        writeStringAndClose(out, "Foo");
        assertEquals("Foo", readAsString(mainFile));
        assertEquals(false, logFile.exists());
    }
    
    @Test
    public void filledStorageRead() throws IOException {
        writeString(mainFile, "Foo");
        storage = new TransactionalStorage(mainFile, logFile);
        assertEquals("Foo", readAsStringAndClose(storage.openRead()));
    }
    
    @Test
    public void filledStorageWrite() throws IOException {
        writeString(mainFile, "Foo");
        storage = new TransactionalStorage(mainFile, logFile);
        writeStringAndClose(storage.openWrite(), "Bar");
        assertEquals("Bar", readAsString(mainFile));
        assertEquals(false, logFile.exists());
    }
    
    @Test
    public void cleanUp() throws IOException {
        writeString(logFile, "Foo");
        storage = new TransactionalStorage(mainFile, logFile);
        assertEquals("Foo", readAsStringAndClose(storage.openRead()));
        assertTrue(mainFile.exists());
        assertFalse(logFile.exists());
    }
    
    @Test
    public void simulateCrashWhenWriting() throws IOException {
        writeString(mainFile, "Foo");
        storage = new TransactionalStorage(mainFile, logFile);
        OutputStream out = storage.openWrite();
        writeString(out, "Bar");
        ((DelegatingOutputStream) out).target().close();
        
        storage = new TransactionalStorage(mainFile, logFile);
        assertEquals("Foo", readAsStringAndClose(storage.openRead()));
        assertEquals(false, logFile.exists());
    }
    
}
