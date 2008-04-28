package com.yoursway.autoupdate.ui;

import static java.lang.String.format;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.yoursway.autoupdate.core.ProposedUpdate;

public class UpdateInformationDialog extends Dialog {
    
    private static final int UPDATE_ID = IDialogConstants.CLIENT_ID + 0;
    
    private static final int LATER_ID = IDialogConstants.CLIENT_ID + 1;
    
    private static final int SKIP_ID = IDialogConstants.CLIENT_ID + 2;
    
    private final IDialogSettings dialogSettings;
    
    private Point minimalSize;
    
    private Label title;
    
    private Browser browser;
    
    UpdateInformationDialogCallback callback;
    
    public UpdateInformationDialog(Shell parentShell, IDialogSettings dialogSettings) {
        super(parentShell);
        if (dialogSettings == null)
            throw new NullPointerException("dialogSettings is null");
        this.dialogSettings = dialogSettings;
        createContent(getShell());
        // without SWT.APPLICATION_MODAL | SWT.MAX | SWT.RESIZE
        setShellStyle(SWT.DIALOG_TRIM | getDefaultOrientation());
    }
    
    public void setCallback(UpdateInformationDialogCallback callback) {
        if (callback == null)
            throw new NullPointerException("callback is null");
        this.callback = callback;
    }
    
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return dialogSettings;
    }
    
    @Override
    protected void configureShell(Shell newShell) {
        newShell.setText("Updates found");
        newShell.addShellListener(new ShellAdapter() {

            public void shellClosed(ShellEvent e) {
                // note: this is not called when Window.close() is called
                callback.postpone();
            }

        });
        super.configureShell(newShell);
    }
    
    @Override
    protected void initializeBounds() {
        super.initializeBounds();
        minimalSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        getShell().setMinimumSize(minimalSize);
    }
    
    @Override
    protected Control createContents(Composite parent) {
        return super.createContents(parent);
    }
    
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        title = new Label(composite, SWT.NONE);
        browser = new Browser(composite, SWT.NONE);
        browser.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).minSize(500, 270).create());
        Label question = new Label(composite, SWT.NONE);
        question.setText("Would you like to update to this version now?");
        GridLayoutFactory.createFrom((GridLayout) composite.getLayout()).generateLayout(composite);
        return composite;
    }
    
    public void setContentsFrom(ProposedUpdate update) {
        title.setText(format("An updated version %s has been found. The changes are:", update
                .targetVersionDisplayName()));
        browser.setText(update.changesDescription());
    }
    
    @Override
    protected Control createButtonBar(Composite parent) {
        Control control = super.createButtonBar(parent);
        control.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER)
                .create());
        return control;
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, LATER_ID, "Remind &Later", false).addSelectionListener(new SelectionAdapter() {
            
            public void widgetSelected(SelectionEvent e) {
                callback.postpone();
                close();
            }
            
        });
        createButton(parent, SKIP_ID, "&Skip This Version", false).addSelectionListener(new SelectionAdapter() {
            
            public void widgetSelected(SelectionEvent e) {
                callback.skip();
                close();
            }
            
        });;
        createSpacer(parent);
        createButton(parent, UPDATE_ID, "&Update Now", true).addSelectionListener(new SelectionAdapter() {
            
            public void widgetSelected(SelectionEvent e) {
                callback.install();
                close();
            }
            
        });
    }
    
    private void createSpacer(Composite parent) {
        ((GridLayout) parent.getLayout()).numColumns++;
        Label label = new Label(parent, SWT.NONE);
        label.setText("");
        label.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
    }
    
    private void createContent(Composite parent) {
        
    }
    
}
