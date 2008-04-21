package com.yoursway.autoupdate.ui;

import org.eclipse.jface.dialogs.IDialogSettings;

public class DialogUtils {
    
    public static IDialogSettings lookup(IDialogSettings parent, String sectionName) {
        IDialogSettings section = parent.getSection(sectionName);
        if (section == null)
            section = parent.addNewSection(sectionName);
        return section;
    }
    
}
