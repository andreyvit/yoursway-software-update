package com.yoursway.autoupdate.ui;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public class DialogUtils {
    
    public static IDialogSettings lookup(IDialogSettings parent, String sectionName) {
        IDialogSettings section = parent.getSection(sectionName);
        if (section == null)
            section = parent.addNewSection(sectionName);
        return section;
    }
    
    public static void centerWindow(Shell shell) {
        Monitor nearestMonitor = shell.getMonitor();
        Rectangle monitorBounds = nearestMonitor.getBounds();
        Point shellSize = shell.getSize();
        shell.setLocation(monitorBounds.x + (monitorBounds.width - shellSize.x) / 2, monitorBounds.y
                + (monitorBounds.height - shellSize.y) / 2);
    }
    
}
