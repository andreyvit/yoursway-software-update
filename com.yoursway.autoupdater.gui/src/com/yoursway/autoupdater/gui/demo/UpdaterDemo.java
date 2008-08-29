package com.yoursway.autoupdater.gui.demo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.yoursway.autoupdater.auxiliary.SuiteDefinition;
import com.yoursway.autoupdater.gui.view.VersionsView;
import com.yoursway.autoupdater.localrepository.LocalRepository;

public class UpdaterDemo {
    
    private static Shell shell;
    
    public static void main(String[] args) {
        Display display = new Display();
        
        shell = new Shell(display);
        shell.setText("Autoupdater");
        shell.setBounds(new Rectangle(480, 320, 320, 240));
        
        SuiteDefinition suite = null;
        LocalRepository localRepository = null;
        try {
            suite = SuiteDefinition.load(args[0], args[1]);
            localRepository = LocalRepository.createForGUI();
        } catch (Throwable e) {
            fatalError(e);
        }
        
        new VersionsView(shell, suite, localRepository, new UpdaterStyleMock(display));
        
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
