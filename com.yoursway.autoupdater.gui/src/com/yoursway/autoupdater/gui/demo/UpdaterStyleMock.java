package com.yoursway.autoupdater.gui.demo;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.yoursway.autoupdater.gui.view.UpdaterStyle;

public class UpdaterStyleMock implements UpdaterStyle {
    
    private final Color red;
    private final Color blue;
    private final Color orange;
    
    public UpdaterStyleMock(Display display) {
        red = new Color(display, 255, 0, 0);
        blue = new Color(display, 0, 0, 255);
        orange = new Color(display, 255, 129, 0);
    }
    
    public Color damagedColor() {
        return red;
    }
    
    public Color localVersionColor() {
        return blue;
    }
    
    public Color failedColor() {
        return orange;
    }
    
}
