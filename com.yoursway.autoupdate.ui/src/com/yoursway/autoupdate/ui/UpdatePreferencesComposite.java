package com.yoursway.autoupdate.ui;

import static com.google.common.collect.Maps.immutableBiMap;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.ProgressBar;

import com.google.common.collect.BiMap;
import com.yoursway.autoupdate.core.glue.state.schedule.Schedule;
import com.yoursway.common.ui.animatedimage.SpinnerControl;

public class UpdatePreferencesComposite extends Composite {
    
    private Button checkDaily;
    private Button checkWeekly;
    private Button checkManually;
    private Button checkNow;
    private Link label;
    private SpinnerControl spinner;
    private Composite buttonAndLabel;
    private BiMap<Schedule, Button> schedulesToRadios;
    private Boolean spinnerShown;
    private Boolean progressShown;
    private UpdatePreferencesCallback callback;
    private Composite progressComposite;
    private ProgressBar progressBar;
    private Link actionLabel;
    private Button stopUpdate;
    
    public UpdatePreferencesComposite(Composite parent, int style) {
        super(parent, style);
        createContent(this);
        checkDaily.setSelection(true);
        schedulesToRadios = immutableBiMap(Schedule.DAILY, checkDaily, Schedule.WEEKLY, checkWeekly,
                Schedule.MANUAL, checkManually);
        SelectionAdapter scheduleSelectionListener = new SelectionAdapter() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                callback.setSchedule(getSchedule());
            }
            
        };
        for (Button button : schedulesToRadios.values())
            button.addSelectionListener(scheduleSelectionListener);
        checkNow.addSelectionListener(new SelectionAdapter() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                callback.checkNow();
            }
            
        });
        
    }
    
    public void setCallback(UpdatePreferencesCallback callback) {
        this.callback = callback;
    }
    
    public void setSchedule(Schedule schedule) {
        Button activeRadio = schedulesToRadios.get(schedule);
        for (Button radio : schedulesToRadios.values())
            if (radio.getSelection() != (radio == activeRadio))
                radio.setSelection(radio == activeRadio);
    }
    
    private Schedule getSchedule() {
        for (Map.Entry<Schedule, Button> entry : schedulesToRadios.entrySet())
            if (entry.getValue().getSelection())
                return entry.getKey();
        throw new AssertionError("No radio button is selected");
    }
    
    public void reportLastCheck(Date date) {
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        hideSpinner();
        hideProgress();
        label.setText("Last check at " + dateFormat.format(date));
    }
    
    public void reportLastFailedCheck(Date date) {
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        hideSpinner();
        hideProgress();
        label.setText("Last failed check at " + dateFormat.format(date));
    }

    public void reportNeverChecked() {
        hideSpinner();
        hideProgress();
        label.setText("");
    }
    
    public void reportChecking() {
        showSpinner();
        hideProgress();
        label.setText("Checking for updates...");
    }

    public void reportInstalling(int progress) {
        showSpinner();
        showProgress();
        label.setText("Installing updates...");
        actionLabel.setText(format("%d%% done", progress));
    }

    private void createContent(Composite parent) {
        checkDaily = new Button(parent, SWT.RADIO);
        checkDaily.setText("Check for updates daily");
        
        checkWeekly = new Button(parent, SWT.RADIO);
        checkWeekly.setText("Check for updates weekly");
        
        checkManually = new Button(parent, SWT.RADIO);
        checkManually.setText("Don't check for updates automatically");
        
        buttonAndLabel = new Composite(parent, SWT.NONE);
        buttonAndLabel.setLayoutData(GridDataFactory.defaultsFor(buttonAndLabel).indent(0, 10).grab(true,
                false).create());
        
        checkNow = new Button(buttonAndLabel, SWT.PUSH);
        checkNow.setText("Check Now");
        
        spinner = new SpinnerControl(buttonAndLabel, SWT.NONE);
        spinner.setDelayOverrideMs(100);
        
        label = new Link(buttonAndLabel, SWT.NONE);
        label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, true)
                .create());
//        label.setText("Last check 5 hours ago");
        
        GridLayoutFactory.fillDefaults().numColumns(3).generateLayout(buttonAndLabel);
        
        progressComposite = new Composite(parent, SWT.NONE);
        progressComposite.setLayoutData(GridDataFactory.defaultsFor(buttonAndLabel).indent(0, 20).grab(true,
                false).create());
        
        actionLabel = new Link(progressComposite, SWT.NONE);
        actionLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, true)
                .span(2, 1).create());
//        actionLabel.setText("Downloading updates...");
        
        progressBar = new ProgressBar(progressComposite, SWT.NONE);
        progressBar.setSelection(20);
        progressBar.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER)
                .create());
        
        stopUpdate = new Button(progressComposite, SWT.PUSH);
        stopUpdate.setText("Stop");
        stopUpdate.setEnabled(false);
        
        GridLayoutFactory.fillDefaults().numColumns(2).spacing(0, 0).margins(10, 0).generateLayout(
                progressComposite);
        
        ((GridData) stopUpdate.getLayoutData()).horizontalIndent = 5;
        
        GridLayoutFactory.swtDefaults().generateLayout(parent);
        
        hideSpinner();
        hideProgress();
    }
    
    private void hideSpinner() {
        if (FALSE == spinnerShown)
            return;
        spinner.setLayoutData(GridDataFactory.fillDefaults().create());
        spinner.setVisible(false);
        checkNow.setEnabled(true);
        buttonAndLabel.layout();
        spinnerShown = FALSE;
    }
    
    private void showSpinner() {
        if (TRUE == spinnerShown)
            return;
        spinner.setLayoutData(GridDataFactory.fillDefaults().indent(25, 0).create());
        spinner.setVisible(true);
        checkNow.setEnabled(false);
        buttonAndLabel.layout();
        spinnerShown = TRUE;
    }
    
    private void hideProgress() {
        if (FALSE == progressShown)
            return;
        progressComposite.setVisible(false);
        progressShown = FALSE;
    }
    
    private void showProgress() {
        if (TRUE == progressShown)
            return;
        progressComposite.setVisible(true);
        progressShown = TRUE;
    }
    
}
