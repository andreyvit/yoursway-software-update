package com.yoursway.autoupdater.gui.view;

import static com.yoursway.utils.assertions.Assert.assertion;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.yoursway.autoupdater.auxiliary.ProductVersion;
import com.yoursway.autoupdater.auxiliary.Suite;
import com.yoursway.autoupdater.localrepository.LocalRepository;
import com.yoursway.autoupdater.localrepository.UpdatingListener;

public class VersionsView {
    
    public VersionsView(Composite parent, Suite suite, final LocalRepository localRepository,
            UpdaterStyle style) {
        
        parent.setLayout(new GridLayout());
        
        final Table versions = new Table(parent, SWT.SINGLE);
        versions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        for (ProductVersion version : suite.versions()) {
            TableItem item = new TableItem(versions, SWT.NONE);
            item.setData(version);
            item.setText(version.toString());
            if (version.damaged())
                item.setForeground(style.damagedColor());
        }
        
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = layout.marginRight = layout.marginHeight = layout.marginBottom = 0;
        panel.setLayout(layout);
        
        final ProgressBar progress = new ProgressBar(panel, SWT.HORIZONTAL);
        progress.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        progress.setVisible(false);
        
        final Button update = new Button(panel, SWT.NONE);
        update.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false));
        update.setText("Update");
        update.setEnabled(false);
        
        //> move following lines to controller
        
        versions.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
                // nothing
            }
            
            public void widgetSelected(SelectionEvent e) {
                TableItem[] selection = versions.getSelection();
                update.setEnabled(selection.length == 1);
            }
        });
        
        update.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
                // nothing
            }
            
            public void widgetSelected(SelectionEvent e) {
                TableItem[] selection = versions.getSelection();
                assertion(selection.length == 1, "Only one item must be selected");
                TableItem item = selection[0];
                ProductVersion version = (ProductVersion) item.getData();
                
                localRepository.startUpdating(version, new UpdatingListener() {
                    public void downloadingStarted() {
                        progress.getDisplay().asyncExec(new Runnable() {
                            public void run() {
                                progress.setSelection(0);
                                progress.setVisible(true);
                            }
                        });
                    }
                    
                    public void downloading(final double p) {
                        progress.getDisplay().asyncExec(new Runnable() {
                            public void run() {
                                int value = (int) (progress.getMaximum() * p);
                                progress.setSelection(value);
                            }
                        });
                    }
                    
                    public void downloadingCompleted() {
                        /*
                        progress.getDisplay().asyncExec(new Runnable() {
                            public void run() {
                                progress.setSelection(progress.getMaximum());
                            }
                        });
                        
                        //> ask user about continuing
                        */

                        progress.getDisplay().asyncExec(new Runnable() {
                            public void run() {
                                progress.setVisible(false);
                            }
                        });
                    }
                });
            }
        });
        
    }
}
