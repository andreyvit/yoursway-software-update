package com.yoursway.autoupdate.core.glue.tests;

import static com.yoursway.autoupdate.core.glue.tests.Variable.anyObjectSavingInto;
import static com.yoursway.utils.YsFileUtils.writeObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.yoursway.autoupdate.core.glue.persister.PersistentState;
import com.yoursway.autoupdate.core.glue.persister.Persister;
import com.yoursway.autoupdate.core.glue.persister.PersisterNonOperational;
import com.yoursway.autoupdate.core.glue.persister.StateFactory;
import com.yoursway.autoupdate.core.glue.persister.StateListener;
import com.yoursway.autoupdate.core.glue.persister.Storage;
import com.yoursway.autoupdate.core.glue.sheduling.RelativeScheduler;

public class PersisterTests {
    
    private static final Long THE_ULTIMATE_ANSWER = new Long(42);
    
    private static final Long ALTERNATIVE_ULTIMATE_ANSWER = new Long(6 * 8);
    
    private static final byte[] THE_ULTIMATE_ANSWER_BYTES = getUltimateAnswerAsBytes(THE_ULTIMATE_ANSWER);
    
    private static final byte[] ALTERNATIVE_ULTIMATE_ANSWER_BYTES = getUltimateAnswerAsBytes(ALTERNATIVE_ULTIMATE_ANSWER);
    
    private Storage storage;
    private RelativeScheduler scheduler;
    private StateFactory factory;
    private PersistentState state;
    
    private Variable<Runnable> runnable;
    
    private ByteArrayOutputStream outputStream;
    
    private Variable<StateListener> stateListener;
    
    @Before
    public void setUp() throws PersisterNonOperational {
        storage = createMock(Storage.class);
        scheduler = createMock(RelativeScheduler.class);
        factory = createMock(StateFactory.class);
        state = createMock(PersistentState.class);
        
        runnable = new Variable<Runnable>(Runnable.class);
        stateListener = Variable.create(StateListener.class);
        
    }
    
    protected void createPersister() throws PersisterNonOperational {
        new Persister(storage, scheduler, factory);
    }
    
    public void replayAll() {
        replay(storage, scheduler, factory, state);
    }
    
    public void verifyAll() {
        verify(storage, scheduler, factory, state);
        reset(storage, scheduler, factory, state);
    }
    
    @Test
    public void isEmptyInitially() throws PersisterNonOperational, IOException {
        storage.openRead();
        expectLastCall().andReturn(emptyByteArray());
        
        factory.createEmptyState();
        expectLastCall().andReturn(state);
        
        state.addListener(anyObjectSavingInto(stateListener));
        replayAll();
        
        createPersister();
        verifyAll();
    }
    
    @Test
    public void schedulesSavingUponChange() throws PersisterNonOperational, IOException {
        isEmptyInitially();
        
        state.createMemento();
        expectLastCall().andReturn(THE_ULTIMATE_ANSWER);
        scheduler.schedule(anyObjectSavingInto(runnable), eq(0));
        replayAll();
        
        stateListener.getValue().stateChanged();
        verifyAll();
    }
    
    @Test
    public void savesStateWhenRunnableIsCalled() throws PersisterNonOperational, IOException {
        schedulesSavingUponChange();
        
        outputStream = new ByteArrayOutputStream();
        storage.openWrite();
        expectLastCall().andReturn(outputStream);
        replayAll();
        
        runnable.extractValue().run();
        verifyAll();
        
        assertArrayEquals(THE_ULTIMATE_ANSWER_BYTES, outputStream.toByteArray());
    }
    
    @Test
    public void readsSavedState() throws PersisterNonOperational, IOException {
        storage.openRead();
        expectLastCall().andReturn(new ByteArrayInputStream(THE_ULTIMATE_ANSWER_BYTES));
        
        factory.createState(eq(THE_ULTIMATE_ANSWER));
        expectLastCall().andReturn(state);
        
        state.addListener(anyObjectSavingInto(stateListener));
        replayAll();
        
        createPersister();
        verifyAll();
    }
    
    @Test
    public void doesNotScheduleSavingOnSubsequentChanges() throws PersisterNonOperational, IOException {
        schedulesSavingUponChange();
        
        state.createMemento();
        expectLastCall().andReturn(ALTERNATIVE_ULTIMATE_ANSWER);
        replayAll();
        
        stateListener.getValue().stateChanged();
        verifyAll();
    }
    
    @Test
    public void savesNewestStateAfterMultipleChanges() throws PersisterNonOperational, IOException {
        doesNotScheduleSavingOnSubsequentChanges();
        
        outputStream = new ByteArrayOutputStream();
        storage.openWrite();
        expectLastCall().andReturn(outputStream);
        replayAll();
        
        runnable.extractValue().run();
        verifyAll();
        
        assertArrayEquals(ALTERNATIVE_ULTIMATE_ANSWER_BYTES, outputStream.toByteArray());
    }
    
    private static byte[] getUltimateAnswerAsBytes(Long answer) {
        try {
            ByteArrayOutputStream correct = new ByteArrayOutputStream();
            writeObject(answer, correct);
            return correct.toByteArray();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
    
    private ByteArrayInputStream emptyByteArray() {
        return new ByteArrayInputStream(new byte[0]);
    }
    
}
