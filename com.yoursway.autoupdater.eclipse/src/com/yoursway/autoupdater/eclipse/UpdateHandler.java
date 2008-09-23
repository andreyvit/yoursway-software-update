package com.yoursway.autoupdater.eclipse;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;

public class UpdateHandler implements IHandler {
    
    public void addHandlerListener(IHandlerListener handlerListener) {
        // TODO Auto-generated method stub
        
    }
    
    public void dispose() {
        // TODO Auto-generated method stub
    }
    
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Activator.controller.updateApplication();
        return null;
    }
    
    public boolean isEnabled() {
        return true;
    }
    
    public boolean isHandled() {
        return true;
    }
    
    public void removeHandlerListener(IHandlerListener handlerListener) {
        // TODO Auto-generated method stub
        
    }
    
}
