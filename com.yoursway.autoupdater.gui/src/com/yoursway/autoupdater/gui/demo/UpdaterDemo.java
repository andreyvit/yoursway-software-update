package com.yoursway.autoupdater.gui.demo;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.yoursway.autoupdater.auxiliary.ComponentStopper;
import com.yoursway.autoupdater.auxiliary.SuiteDefinition;
import com.yoursway.autoupdater.auxiliary.UpdatableApplication;
import com.yoursway.autoupdater.gui.view.VersionsView;
import com.yoursway.autoupdater.localrepository.LocalRepository;

public class UpdaterDemo {
    
    private static Shell shell;
    
    public static void main(final String[] args) {
        Display display = new Display();
        
        shell = new Shell(display);
        shell.setText("Autoupdater");
        shell.setBounds(new Rectangle(480, 320, 320, 240));
        
        UpdatableApplication app = new UpdatableApplication() {
            public SuiteDefinition suite() {
                try {
                    return SuiteDefinition.load(args[0], args[1]);
                } catch (Throwable e) {
                    fatalError(e);
                    return null;
                }
            }
            
            public LocalRepository localRepository() {
                try {
                    return LocalRepository.createForGUI(this);
                } catch (Throwable e) {
                    fatalError(e);
                    return null;
                }
            }
            
            public File rootFolder(String productName) {
                throw new UnsupportedOperationException();
            }
            
            public ComponentStopper componentStopper(String productName) {
                return new ComponentStopper() {
                    public boolean stop() {
                        System.exit(0);
                        return true;
                    }
                };
            }
        };
        
        new VersionsView(shell, app, new UpdaterStyleMock(display));
        
        shell.open();
        
        while (!shell.isDisposed()) {
            try {
                if (!display.readAndDispatch())
                    display.sleep();
            } catch (Throwable throwable) {
                throwable.printStackTrace(System.err);
            }
        }
        
        display.dispose();
    }
    
    private static void fatalError(Throwable e) {
        MessageBox msg = new MessageBox(shell, SWT.NONE);
        StringBuilder sb = new StringBuilder();
        for (Throwable _e = e; _e != null; _e = _e.getCause())
            sb.append(e.getClass().getSimpleName() + ": " + e.getMessage() + ".\n");
        msg.setMessage(sb.toString());
        msg.open();
        System.exit(-1);
    }
}
