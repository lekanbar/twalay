package com.dway.twalay;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;

public class ColorLabel extends LabelField
{
    private int color;
    
    public ColorLabel(Object text, long style, int color)
    {
        super(text, style);
        
        this.color = color;
    }
    
    public void paint(Graphics g)
    {
        g.setColor(color);
        super.paint(g);
    }    
} 