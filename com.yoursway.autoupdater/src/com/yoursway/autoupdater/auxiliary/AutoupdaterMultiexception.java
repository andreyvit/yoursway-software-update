package com.yoursway.autoupdater.auxiliary;

import static com.google.common.collect.Lists.immutableList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.yoursway.utils.assertions.Assert.assertion;

import java.util.List;

public class AutoupdaterMultiexception extends AutoupdaterException {
    private static final long serialVersionUID = 2817534076440171524L;
    
    private final List<AutoupdaterException> exceptions;
    
    private AutoupdaterMultiexception(List<AutoupdaterException> exceptions) {
        super("Several exceptions");
        assertion(exceptions.size() > 1, "You should use an usual exception here");
        this.exceptions = exceptions;
    }
    
    public List<AutoupdaterException> exceptions() {
        return immutableList(exceptions);
    }
    
    public static <T> void _for(Iterable<T> iterable, MEDoBlock<T> block) throws AutoupdaterException {
        List<AutoupdaterException> exceptions = null;
        
        for (T _ : iterable) {
            try {
                block._do(_);
            } catch (AutoupdaterException e) {
                if (exceptions == null)
                    exceptions = newLinkedList();
                exceptions.add(e);
            }
        }
        
        if (exceptions != null) {
            if (exceptions.size() == 1)
                throw exceptions.get(0);
            throw new AutoupdaterMultiexception(exceptions);
        }
    }
    
}
