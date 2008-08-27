package com.yoursway.autoupdater.gui.demo;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.yoursway.autoupdater.gui.view.UpdaterStyle;

public class UpdaterStyleMock implements UpdaterStyle {
    
    private final Color damagedColor;
    
    public UpdaterStyleMock(Display display) {
        damagedColor = new Color(display, 255, 0, 0);
    }
    
    public Color damagedColor() {
        return damagedColor;
    }
    
}
