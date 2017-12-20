/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util.curves;

import java.awt.Color;
import java.awt.Font;

/**
 * @author Raphaël
 */

public class RuleTick
{
    protected double increment; // number of units represented by one tick
    protected double incrementOffset; // number of units to shift from the start
    protected int graphicLength;
    protected int minDistForVisibility; /* the min number of pixels between two 
    ticks so that they are painted */
    protected Color tickColor;
    
    protected boolean labelled;
    protected Font labelFont;
    protected Color labelColor;
    protected int spaceBetweenLabelAndTick;
    
    public RuleTick(double increment, int graphicLength, boolean labelled)
    {
        this.increment = increment;
        incrementOffset = 0;
        this.graphicLength = graphicLength;
        minDistForVisibility = 10;
        tickColor = Color.BLACK;
        
        this.labelled = labelled;
        labelFont = new Font("SansSerif", Font.PLAIN, 10);
        labelColor = Color.BLACK;
        spaceBetweenLabelAndTick = 4;
    }
    
    public double getIncrement()
    {
        return increment;
    }
    
    public void setIncrement(double increment)
    {
        this.increment = increment;
    }
    
    public double getIncrementOffset()
    {
        return incrementOffset;
    }
    
    public void setIncrementOffset(double incrementOffset)
    {
        this.incrementOffset = incrementOffset;
    }
    
    public int getGraphicLength()
    {
        return graphicLength;
    }
    
    public void setGraphicLength(int graphicLength)
    {
        this.graphicLength = graphicLength;
    }
    
    public int getMinDistForVisibility()
    {
        return minDistForVisibility;
    }
    
    public void setMinDistForVisibility(int minDistForVisibility)
    {
        this.minDistForVisibility = minDistForVisibility;
    }
    
    public Color getTickColor()
    {
        return tickColor;
    }
    
    public void setTickColor(Color tickColor)
    {
        this.tickColor = tickColor;
    }
    
    public boolean isLabelled()
    {
        return labelled;
    }
    
    public void setLabelled(boolean labelled)
    {
        this.labelled = labelled;
    }
    
    public Font getLabelFont()
    {
        return labelFont;
    }
    
    public void setLabelFont(Font labelFont)
    {
        this.labelFont = labelFont;
    }
    
    public Color getLabelColor()
    {
        return labelColor;
    }
    
    public void setLabelColor(Color labelColor)
    {
        this.labelColor = labelColor;
    }
    
    public void setColor(Color color)
    {
        tickColor = color;
        labelColor = color;
    }
    
    public int getSpaceBetweenLabelAndTick()
    {
        return spaceBetweenLabelAndTick;
    }
    
    public void setSpaceBetweenLabelAndTick(int spaceBetweenLabelAndTick)
    {
        this.spaceBetweenLabelAndTick = spaceBetweenLabelAndTick;
    }
}
