package com.yoursway.autoupdater.gui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.yoursway.autoupdater.auxiliary.ProductVersion;
import com.yoursway.autoupdater.auxiliary.Suite;

public class VersionsView {
    
    public VersionsView(Composite parent, Suite suite, UpdaterStyle style) {
        parent.setLayout(new GridLayout());
        
        Table versions = new Table(parent, SWT.NONE);
        versions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        for (ProductVersion version : suite.versions()) {
            TableItem item = new TableItem(versions, SWT.NONE);
            item.setData(version);
            item.setText(version.toString());
            if (version.damaged())
                item.setForeground(style.damagedColor());
        }
        
        Button update = new Button(parent, SWT.NONE);
        update.setText("Update");
        update.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false));
        
    }
}
