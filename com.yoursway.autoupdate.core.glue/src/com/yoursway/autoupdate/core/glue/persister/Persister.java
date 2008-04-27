package com.yoursway.autoupdate.core.glue.persister;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.yoursway.autoupdate.core.glue.sheduling.RelativeScheduler;

public class Persister {
    
    private final Storage storage;
    
    private final RelativeScheduler scheduler;
    
    private final RetryTimer timer = new RetryTimer();
    
    private boolean writeScheduled = false;
    
    private Object memento;
    
    private PersistentState state;
    
    public Persister(Storage storage, RelativeScheduler scheduler, StateFactory stateFactory)
            throws PersisterNonOperational {
        if (storage == null)
            throw new NullPointerException("storage is null");
        if (scheduler == null)
            throw new NullPointerException("scheduler is null");
        if (stateFactory == null)
            throw new NullPointerException("stateFactory is null");
        this.storage = storage;
        this.scheduler = scheduler;
        
        Object memento = read();
        if (memento == null)
            state = stateFactory.createEmptyState();
        else
            state = stateFactory.createState(memento);
        
        state.addListener(new StateListener() {
            
            public void stateChanged() {
                scheduleWriteOnChange(state.createMemento());
            }
            
        });
    }
    
    public PersistentState state() {
        return state;
    }
    
    synchronized void scheduleWriteOnChange(Object memento) {
        this.memento = memento;
        if (writeScheduled)
            return;
        scheduleWrite();
    }
    
    private void scheduleWrite() {
        writeScheduled = true;
        scheduler.schedule(new Runnable() {
            
            public void run() {
                writeScheduled = false;
                write();
            }
            
        }, (int) timer.delayUntilNextAttempOrZero());
    }
    
    synchronized void write() {
        try {
            OutputStream out = storage.openWrite();
            try {
                ObjectOutputStream oos = new ObjectOutputStream(out);
                oos.writeObject(memento);
                oos.flush();
            } finally {
                out.close();
            }
            timer.successfulAttemp();
        } catch (IOException e) {
            timer.failedAttemp();
            scheduleWrite();
        }
    }
    
    private Object read() throws PersisterNonOperational {
        try {
            return doRead();
        } catch (ClassNotFoundException e) {
            return doTrashAndRetryRead();
        } catch (IOException e) {
            return doTrashAndRetryRead();
        }
    }
    
    private Object doRead() throws IOException, ClassNotFoundException {
        InputStream in = storage.openRead();
        try {
            in.mark(1);
            if (-1 == in.read())
                return null;
            in.reset();
            ObjectInputStream oin = new ObjectInputStream(in);
            return oin.readObject();
        } finally {
            in.close();
        }
    }
    
    private Object doTrashAndRetryRead() throws PersisterNonOperational {
        try {
            storage.trash();
        } catch (IOException e) {
            throw new PersisterNonOperational(e);
        }
        System.out.println("Trashed the state to avoid reading errors.");
        try {
            return doRead();
        } catch (IOException e) {
            throw new PersisterNonOperational(e);
        } catch (ClassNotFoundException e) {
            throw new PersisterNonOperational(e);
        }
    }
    
}
