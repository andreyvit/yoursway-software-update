package com.yoursway.autoupdater.gui.view;

import static com.yoursway.utils.assertions.Assert.assertion;
import static org.eclipse.jface.layout.GridLayoutFactory.swtDefaults;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.yoursway.autoupdater.auxiliary.AutoupdaterException;
import com.yoursway.autoupdater.auxiliary.ProductVersionDefinition;
import com.yoursway.autoupdater.auxiliary.SuiteDefinition;
import com.yoursway.autoupdater.auxiliary.UpdatableApplicationView;
import com.yoursway.autoupdater.gui.demo.UpdaterStyleMock;
import com.yoursway.autoupdater.localrepository.LocalRepository;
import com.yoursway.autoupdater.localrepository.UpdatingListener;

public class UpdaterView {
    
    private final LocalRepository repo;
    private final UpdatableApplicationView appView;
    
    private final Shell shell;
    private final Table versions;
    private final ProgressBar progress;
    private final Button update;
    
    public UpdaterView(Shell shell, final UpdatableApplicationView appView, SuiteDefinition suite,
            LocalRepository repo, UpdaterStyle style) {
        
        if (repo == null)
            throw new NullPointerException("repo is null");
        if (appView == null)
            throw new NullPointerException("appView is null");
        this.repo = repo;
        this.appView = appView;
        
        shell.setLayout(new GridLayout());
        this.shell = shell;
        
        versions = new Table(shell, SWT.SINGLE);
        versions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        for (ProductVersionDefinition version : suite.versions()) {
            TableItem item = new TableItem(versions, SWT.NONE);
            item.setData(version);
            item.setText(version.toString());
            
            if (repo.hasLocalVersion(version)) {
                if (repo.isVersionFailed(version))
                    item.setForeground(style.failedColor());
                
                repo.addUpdatingListener(version, new ProgressBarUpdatingListener());
            }
            
            if (version.damaged())
                item.setForeground(style.damagedColor());
            
        }
        
        Composite panel = new Composite(shell, SWT.NONE);
        panel.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
        panel.setLayout(swtDefaults().margins(0, 0).extendedMargins(0, 0, 0, 0).numColumns(2).create());
        
        progress = new ProgressBar(panel, SWT.HORIZONTAL);
        progress.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        progress.setVisible(false);
        
        update = new Button(panel, SWT.NONE);
        update.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false));
        update.setText("Update");
        update.setEnabled(false);
        
        //> move following lines to controller
        
        versions.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
                TableItem[] selection = versions.getSelection();
                if (selection.length == 1)
                    updateToSelected();
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
                updateToSelected();
            }
        });
        
    }
    
    public static UpdaterViewFactory factory() {
        return new UpdaterViewFactory() {
            
            public UpdaterView createView(UpdatableApplicationView appView, SuiteDefinition suite,
                    LocalRepository repo) {
                
                Shell shell = new Shell();
                //! magic
                shell.setText("Autoupdater");
                shell.setBounds(new Rectangle(480, 320, 320, 240));
                
                UpdaterStyleMock style = new UpdaterStyleMock(shell.getDisplay());
                UpdaterView view = new UpdaterView(shell, appView, suite, repo, style);
                
                return view;
            }
        };
    }
    
    public void show() {
        shell.open();
    }
    
    private void updateToSelected() {
        TableItem[] selection = versions.getSelection();
        assertion(selection.length == 1, "Only one item must be selected");
        TableItem item = selection[0];
        ProductVersionDefinition version = (ProductVersionDefinition) item.getData();
        
        try {
            repo.startUpdating(version, new ProgressBarUpdatingListener());
        } catch (AutoupdaterException e) {
            appView.displayAutoupdaterErrorMessage(e);
        }
    }
    
    private final class ProgressBarUpdatingListener implements UpdatingListener {
        public void downloadingStarted() {
            if (progress.isDisposed())
                return;
            
            progress.getDisplay().asyncExec(new Runnable() {
                public void run() {
                    if (progress.isDisposed())
                        return;
                    
                    progress.setSelection(0);
                    progress.setVisible(true);
                }
            });
        }
        
        public void downloading(final double p) {
            if (progress.isDisposed())
                return;
            
            progress.getDisplay().asyncExec(new Runnable() {
                public void run() {
                    if (progress.isDisposed())
                        return;
                    
                    int value = (int) (progress.getMaximum() * p);
                    progress.setSelection(value);
                    progress.setVisible(true);
                }
            });
            
        }
        
        public void downloadingCompleted() {
            if (progress.isDisposed())
                return;
            
            /*
            progress.getDisplay().asyncExec(new Runnable() {
                public void run() {
                    if (progress.isDisposed())
                        return;
                    progress.setSelection(progress.getMaximum());
                }
            });
            
            //> ask user about continuing
            */

            progress.getDisplay().asyncExec(new Runnable() {
                public void run() {
                    if (progress.isDisposed())
                        return;
                    
                    progress.setVisible(false);
                    //? set progress to indeterminate instead of hiding
                }
            });
        }
    }
}
