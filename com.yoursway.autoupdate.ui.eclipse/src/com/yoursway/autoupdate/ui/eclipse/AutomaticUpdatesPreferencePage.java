package com.yoursway.autoupdate.ui.eclipse;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.yoursway.autoupdate.ui.UpdatePreferencesComposite;

public class AutomaticUpdatesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    
    public AutomaticUpdatesPreferencePage() {
    }
    
    public AutomaticUpdatesPreferencePage(String title) {
        super(title);
    }
    
    public AutomaticUpdatesPreferencePage(String title, ImageDescriptor image) {
        super(title, image);
    }
    
    @Override
    protected Control createContents(Composite parent) {
        UpdatePreferencesComposite composite = new UpdatePreferencesComposite(parent, SWT.NONE);
        composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        
        AutomaticUpdatesSchedulingStartup.instance().add(composite);
        return composite;
    }
    
    public void init(IWorkbench workbench) {
    }
    
}
