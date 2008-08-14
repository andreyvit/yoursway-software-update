package xxx;

import java.util.Collection;

import xxx.library.FileLibrary;

public class OrderManager {
    
    private FileLibrary fileLibrary;
    
    // listeners
    
    public void orderChanged() {
        Collection<Request> requests = null; //!
        
        fileLibrary.order(requests);
    }
}
