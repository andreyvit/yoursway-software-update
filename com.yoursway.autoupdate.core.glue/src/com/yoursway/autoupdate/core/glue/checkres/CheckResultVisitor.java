package com.yoursway.autoupdate.core.glue.checkres;

public interface CheckResultVisitor {
    
    void noUpdatesFound();
    
    void updateFound(UpdateFoundCheckResult result);
    
    void noWriteAccess();
    
    void communicationError(CommunicationErrorCheckResult result);
    
    void internalFailure(InternalFailureCheckResult result);

    void shutdownOccured();
    
}
