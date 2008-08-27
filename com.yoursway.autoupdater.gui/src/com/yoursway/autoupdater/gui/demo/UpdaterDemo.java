package com.yoursway.autoupdater.gui.demo;

import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.yoursway.autoupdater.auxiliary.Suite;
import com.yoursway.autoupdater.gui.view.VersionsView;

public class UpdaterDemo {
    
    public static void main(String[] args) {
        Display display = new Display();
        
        Shell shell = new Shell(display);
        shell.setText("Autoupdater");
        shell.setBounds(new Rectangle(480, 320, 320, 240));
        
        Suite suite = null;
        try {
            URL updateSite = new URL(args[0]);
            String suiteName = args[1];
            suite = new Suite(updateSite, suiteName);
        } catch (Throwable e) {
            MessageBox msg = new MessageBox(shell, SWT.NONE);
            msg.setMessage("Cannot load suite.\n" + e.getClass().getSimpleName() + ": " + e.getMessage());
            msg.open();
        }
        new VersionsView(shell, suite, new UpdaterStyleMock(display));
        
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
}
