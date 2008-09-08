package com.yoursway.autoupdater.gui.demo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.yoursway.autoupdater.auxiliary.AutoupdaterException;
import com.yoursway.autoupdater.auxiliary.SuiteDefinition;
import com.yoursway.autoupdater.auxiliary.UpdatableApplication;
import com.yoursway.autoupdater.auxiliary.UpdatableApplicationProductFeatures;
import com.yoursway.autoupdater.gui.controller.UpdaterController;
import com.yoursway.autoupdater.gui.view.VersionsView;
import com.yoursway.autoupdater.gui.view.VersionsViewFactory;
import com.yoursway.autoupdater.localrepository.LocalRepository;

public class UpdaterDemo {
    
    private static Shell shell;
    
    public static void main(final String[] args) {
        final Display display = new Display();
        
        shell = new Shell(display);
        shell.setText("Autoupdater");
        shell.setBounds(new Rectangle(480, 320, 320, 240));
        
        UpdatableApplication app = new UpdatableApplication() {
            
            public String updateSite() {
                return args[0];
            }
            
            public String suiteName() {
                return args[1];
            }
            
            public boolean inInstallingState() {
                return false;
            }
            
            public void setInstallingState(boolean value) {
                // nothing
            }
            
            public UpdatableApplicationProductFeatures getFeatures(String productName) {
                return UpdatableApplicationProductFeatures.MOCK;
            }
            
        };
        
        VersionsViewFactory viewFactory = new VersionsViewFactory() {
            public VersionsView createView(UpdatableApplication app, SuiteDefinition suite,
                    LocalRepository repo) {
                return new VersionsView(shell, app, suite, repo, new UpdaterStyleMock(display));
            }
        };
        UpdaterController controller = new UpdaterController(app, viewFactory);
        try {
            controller.updateApplication();
        } catch (AutoupdaterException e) {
            fatalError(e);
        }
        
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
