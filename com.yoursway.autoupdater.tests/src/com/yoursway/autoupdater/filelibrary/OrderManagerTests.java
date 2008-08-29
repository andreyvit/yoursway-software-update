package com.yoursway.autoupdater.filelibrary;

import static com.yoursway.autoupdater.filelibrary.RequestUtils.requests;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;

import com.yoursway.autoupdater.filelibrary.FileLibrary;
import com.yoursway.autoupdater.filelibrary.FileLibraryOrder;
import com.yoursway.autoupdater.filelibrary.LibrarySubscriber;
import com.yoursway.autoupdater.filelibrary.OrderManager;

public class OrderManagerTests {
    
    private FileLibrary fileLibrary;
    private OrderManager orderManager;
    
    @Before
    public void setup() {
        fileLibrary = createMock(FileLibrary.class);
        orderManager = new OrderManager(fileLibrary);
    }
    
    @Test
    public void orderChanged_empty() {
        fileLibrary.order(eq(new FileLibraryOrder()));
        replay(fileLibrary);
        
        orderManager.orderChanged();
        verify(fileLibrary);
    }
    
    @Test
    public void orderChanged() throws MalformedURLException {
        LibrarySubscriber s1 = createMock(LibrarySubscriber.class);
        LibrarySubscriber s2 = createMock(LibrarySubscriber.class);
        LibrarySubscriber s3 = createMock(LibrarySubscriber.class);
        
        expect(s1.libraryRequests()).andReturn(requests(1, 3));
        expect(s2.libraryRequests()).andReturn(requests(5, 8));
        expect(s3.libraryRequests()).andReturn(requests(3, 6));
        fileLibrary.order(new FileLibraryOrder(requests(1, 8)));
        replay(fileLibrary, s1, s2, s3);
        
        orderManager.register(s1);
        orderManager.register(s2);
        orderManager.register(s3);
        orderManager.orderChanged();
        verify(fileLibrary, s1, s2, s3);
    }
}
